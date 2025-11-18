package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

class DockerComposeCmdImpl implements DockerComposeCmd.Exec {

    @NotNull
    private final List<@NotNull String> files = new ArrayList<>();
    private String projectName;
    private boolean detached = false;
    @NotNull
    private final Map<@NotNull String, @NotNull String> environment = new HashMap<>();
    private File workingDirectory;
    private long timeout = -1;


    @Override
    @NotNull
    public DockerComposeCmd.Exec withFile(@NotNull File file) {
        this.files.add(file.getAbsolutePath());
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withFiles(@NotNull List<File> files) {
        this.files.addAll(files.stream().map(File::getAbsolutePath).toList());
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withProjectName(@NotNull String projectName) {
        this.projectName = projectName;
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withDetached(boolean detached) {
        this.detached = detached;
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withEnv(@NotNull String key, @NotNull String value) {
        this.environment.put(key, value);
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withWorkingDirectory(@NotNull File workingDir) {
        this.workingDirectory = workingDir;
        return this;
    }

    @Override
    @NotNull
    public DockerComposeCmd.Exec withTimeout(@NotNull Duration timeout) {
        this.timeout = timeout.toMillis();
        return this;
    }

    @Override
    @NotNull
    public DockerComposeResult up() {
        List<String> args = new ArrayList<>();
        if (detached) {
            args.add("-d");
        }
        return exec("up", args.toArray(new String[0]));
    }

    @Override
    @NotNull
    public DockerComposeResult down() {
        return exec("down");
    }

    @Override
    @NotNull
    public DockerComposeResult ps() {
        return exec("ps");
    }

    @Override
    @NotNull
    public DockerComposeResult logs() {
        return exec("logs");
    }

    @Override
    @NotNull
    public DockerComposeResult exec(@NotNull String command, String... args) {
        List<String> cmdList = buildCommand(command, args);
        return executeCommand(cmdList);
    }

    @NotNull
    private List<@NotNull String> buildCommand(@NotNull String command, @NotNull String... args) {
        List<String> cmdList = new ArrayList<>();

        // Docker Compose V2 uses "docker compose" not "docker-compose"
        cmdList.add("docker");
        cmdList.add("compose");

        // Add global options before the command (up/down/etc)
        for (String file : files) {
            cmdList.add("--file");
            cmdList.add(file);
        }

        if (projectName != null && !projectName.isEmpty()) {
            cmdList.add("--project-name");
            cmdList.add(projectName);
        }

        // Add the main command (up, down, etc.)
        cmdList.add(command);

        // Add command-specific arguments
        Collections.addAll(cmdList, args);

        return cmdList;
    }

    @NotNull
    private DockerComposeResult executeCommand(@NotNull List<@NotNull String> cmdList) {
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
            pb.inheritIO();

            process = pb.start();

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
                        exitCode = 124;
                    } else
                        exitCode = process.exitValue();
                }
                Runtime.getRuntime().removeShutdownHook(shutdownHook);

                String message = timedOut
                        ? "Command timed out after " + Duration.ofMillis(timeout).toSeconds() + " seconds"
                        : "Command executed successfully";

                return new DockerComposeResult(
                        exitCode,
                        message,
                        exitCode == 0,
                        String.join(" ", cmdList)
                );
            } catch (InterruptedException e) {
                process.destroy();
                Thread.currentThread().interrupt();
                return new DockerComposeResult(
                        130, // Standard exit code for SIGINT
                        "Command interrupted by user",
                        false,
                        String.join(" ", cmdList)
                );
            } finally {
                try {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                } catch (IllegalStateException e) {
                    // Shutdown in progress, ignore
                }
            }

        } catch (IOException e) {
            return new DockerComposeResult(
                    -1,
                    "Error executing command: " + e.getMessage(),
                    false,
                    String.join(" ", cmdList)
            );
        }
    }

    @NotNull
    private static Thread getShutdownHook(final @NotNull Process process) {
        return new Thread(() -> {
            if (process.isAlive()) {
                process.destroy();
                try {
                    // Give it a moment to terminate gracefully
                    if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                    }
                } catch (InterruptedException e) {
                    process.destroyForcibly();
                }
            }
        });
    }
}