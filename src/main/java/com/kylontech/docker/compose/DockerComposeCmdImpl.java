package com.kylontech.docker.compose;

import com.kylontech.docker.compose.exception.DockerComposeBinaryNotFoundException;
import com.kylontech.docker.compose.exception.DockerComposeException;
import com.kylontech.docker.compose.exception.DockerComposeInterruptedException;
import com.kylontech.docker.compose.exception.DockerComposeTimeoutException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

abstract class DockerComposeCmdImpl<T extends DockerComposeCmdImpl<T>> extends DockerComposeCmd.Exec<T> {

    @NotNull
    private final List<@NotNull String> files = new ArrayList<>();
    private String projectName;
    @NotNull
    private final Map<@NotNull String, @NotNull String> environment = new HashMap<>();
    private File workingDirectory;
    private long timeout = -1;

    @Override
    @NotNull
    public T withFile(@NotNull File file) {
        this.files.add(file.getAbsolutePath());
        return self();
    }

    @Override
    @NotNull
    public T withFiles(@NotNull List<File> files) {
        this.files.addAll(files.stream().map(File::getAbsolutePath).toList());
        return self();
    }

    @Override
    @NotNull
    public T withProjectName(@NotNull String projectName) {
        this.projectName = projectName;
        return self();
    }

    @Override
    @NotNull
    public T withEnv(@NotNull String key, @NotNull String value) {
        this.environment.put(key, value);
        return self();
    }

    @Override
    @NotNull
    public T withWorkingDirectory(@NotNull File workingDir) {
        this.workingDirectory = workingDir;
        return self();
    }

    @Override
    @NotNull
    public T withTimeout(@NotNull Duration timeout) {
        this.timeout = timeout.toMillis();
        return self();
    }

    @NotNull
    public final DockerComposeResult exec() throws DockerComposeException {
        List<String> cmdList = buildCommand();
        return executeCommand(cmdList);
    }

    @NotNull
    private List<@NotNull String> buildCommand() {
        List<String> cmdList = new ArrayList<>();

        cmdList.add("docker");
        cmdList.add("compose");

        // Add global options before the command
        for (String file : files) {
            cmdList.add("--file");
            cmdList.add(file);
        }

        if (projectName != null && !projectName.isEmpty()) {
            cmdList.add("--project-name");
            cmdList.add(projectName);
        }

        buildSubCommand(cmdList);

        return cmdList;
    }

    /**
     * Add command and command-specific options. */
    abstract protected void buildSubCommand(final @NotNull List<@NotNull String> cmdList);

    @NotNull
    private DockerComposeResult executeCommand(final @NotNull List<@NotNull String> cmdList)
            throws DockerComposeException {
        Process process;
        try {
            ProcessBuilder pb = new ProcessBuilder(cmdList);

            if (workingDirectory != null) {
                pb.directory(workingDirectory);
            }

            if (!environment.isEmpty()) {
                Map<String, String> env = pb.environment();
                env.putAll(environment);
            }

            // Inherit IO for foreground mode. This allows Ctrl+C to work.
            // Removed so we can capture stdout/stderr of the process asynchronously.
            // pb.inheritIO();

            process = pb.start();

            ProcessOutputGobbler outputGobbler = new ProcessOutputGobbler(process);
            outputGobbler.start();

            // Add shutdown hook to clean up on interruption
            Thread shutdownHook = getShutdownHook(process);
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            try {
                int exitCode;
                boolean timedOut = false;
                if (timeout < 0)
                    exitCode = process.waitFor();
                else {
                    timedOut = !process.waitFor(timeout, TimeUnit.MILLISECONDS);
                    if (timedOut) {
                        shutdownHook.start();
                        shutdownHook.join();
                        exitCode = 130;
                    } else
                        exitCode = process.exitValue();
                }
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
                outputGobbler.stop();
                String stdout = outputGobbler.getStdout();
                String stderr = outputGobbler.getStderr();

                DockerComposeResult result = new DockerComposeResult(
                        exitCode,
                        stdout,
                        stderr,
                        String.join(" ", cmdList)
                );
                if (timedOut) {
                    throw new DockerComposeTimeoutException(
                            "Command timed out after " + Duration.ofMillis(timeout).toSeconds() + " seconds",
                            result
                    );
                }
                return result;
            } catch (InterruptedException e) {
                process.destroy();
                Thread.currentThread().interrupt();
                throw new DockerComposeInterruptedException(
                        "command interrupted",
                        e,
                        new DockerComposeResult(
                                process.exitValue(), // Standard exit code for SIGINT
                                outputGobbler.getStdout(),
                                outputGobbler.getStderr(),
                                String.join(" ", cmdList))
                );
            } finally {
                try {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                } catch (IllegalStateException e) {
                    // Shutdown in progress, ignore
                }
            }

        } catch (IOException e) {
            throw new DockerComposeBinaryNotFoundException("docker compose not found on host");
        }
    }

    @NotNull
    private static Thread getShutdownHook(final @NotNull Process process) {
        return new Thread(() -> {
            if (process.isAlive()) {
                process.destroy();
                try {
                    // Give it a moment to terminate gracefully
                    if (!process.waitFor(5, TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                    }
                } catch (InterruptedException e) {
                    process.destroyForcibly();
                }
            }
        });
    }
}

class ProcessOutputGobbler {
    @NotNull
    private final Process process;
    @NotNull
    private final Thread outThread;
    @NotNull
    private final Thread errThread;
    @NotNull
    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    @NotNull
    private final ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    public ProcessOutputGobbler(final @NotNull Process process) {
        this.process = process;
        this.outThread = new Thread(() -> {
            try (InputStream is = this.process.getInputStream()) {
                is.transferTo(stdout);
            } catch (IOException ignored) {}
        });
        outThread.setDaemon(true);
        this.errThread = new Thread(() -> {
            try (InputStream es = this.process.getErrorStream()) {
                es.transferTo(stderr);
            } catch (IOException ignored) {}
        });
        errThread.setDaemon(true);
    }

    public void start() {
        this.outThread.start();
        this.errThread.start();
    }

    public void stop() throws InterruptedException, IOException {
        this.outThread.join();
        this.stdout.close();
        this.errThread.join();
        this.stderr.close();
    }

    @NotNull
    public String getStdout() {
        return this.stdout.toString(StandardCharsets.UTF_8);
    }

    @NotNull
    public String getStderr() {
        return this.stderr.toString(StandardCharsets.UTF_8);
    }
}
