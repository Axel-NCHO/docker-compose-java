package com.kylontech.docker.compose;

import com.kylontech.docker.compose.exception.DockerComposeTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DockerComposeCmdTest {

    private DockerComposeClient dockerComposeClient;
    private File workDir;
    private File testUpComposeFile;
    private File testUpWithEnvComposeFile;
    private File testStdout;
    private File testStderr;

    @BeforeEach
    void setup() throws URISyntaxException {
        dockerComposeClient = DockerComposeClientBuilder.getInstance().build();
        testUpComposeFile = Paths.get(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("compose-example/test-up.yml")
                ).toURI()
        ).toFile();
        testUpWithEnvComposeFile = Paths.get(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("compose-example/test-up-with-env-vars.yml")
                ).toURI()
        ).toFile();
        testStdout = Paths.get(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("compose-example/test-stdout.yml")
                ).toURI()
        ).toFile();
        testStderr = Paths.get(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("compose-example/test-stderr.yml")
                ).toURI()
        ).toFile();
        workDir = testUpComposeFile.toPath().getParent().toFile();
    }

    @Test
    void testDockerComposeUpCmdWithWorkDir() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withWorkingDirectory(workDir)
                .exec();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpWithWorkDirNoTimeout() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withWorkingDirectory(workDir)
                .exec();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithFile() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withFile(testUpComposeFile)
                .exec();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithWorkDirAndFile() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withWorkingDirectory(workDir)
                .withFile(testUpComposeFile)
                .withTimeout(Duration.ofSeconds(20L))
                .exec();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithEnv() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withFile(testUpWithEnvComposeFile)
                .withEnv("TEST_VAR", "expected")
                .exec();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeTimedOut() {
        DockerComposeTimeoutException ex = assertThrows(
                DockerComposeTimeoutException.class,
                () -> dockerComposeClient.upCmd()
                        .withTimeout(Duration.ofMillis(200))
                        .withFile(testUpComposeFile)
                        .exec()
        );
        assertTrue(Objects.requireNonNull(ex.result).interrupted());
    }

    @Test
    void testDockerComposeStdOut() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withFile(testStdout)
                .exec();
        assertTrue(result.stdout().contains("Hello-out"));
    }

    @Test
    void testDockerComposeStdErr() {
        DockerComposeResult result = dockerComposeClient.upCmd()
                .withFile(testStderr)
                .exec();
        assertTrue(result.stderr().contains("Hello-err"));
    }
}
