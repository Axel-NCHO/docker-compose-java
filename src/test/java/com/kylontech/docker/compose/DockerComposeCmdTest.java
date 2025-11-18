package com.kylontech.docker.compose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DockerComposeCmdTest {

    private DockerComposeClient dockerComposeClient;
    private File workDir;
    private File testUpComposeFile;
    private File testUpWithEnvComposeFile;

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
        workDir = testUpComposeFile.toPath().getParent().toFile();
    }

    @Test
    void testDockerComposeUpCmdWithWorkDir() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withWorkingDirectory(workDir)
                .withTimeout(Duration.ofSeconds(20L))
                .up();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpWithWorkDirNoTimeout() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withWorkingDirectory(workDir)
                .up();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithFile() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withFile(testUpComposeFile)
                .withTimeout(Duration.ofSeconds(20L))
                .up();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithWorkDirAndFile() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withWorkingDirectory(workDir)
                .withFile(testUpComposeFile)
                .withTimeout(Duration.ofSeconds(20L))
                .up();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeUpCmdWithEnv() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withFile(testUpWithEnvComposeFile)
                .withTimeout(Duration.ofSeconds(20L))
                .withEnv("TEST_VAR", "expected")
                .up();
        assertTrue(result.success());
    }

    @Test
    void testDockerComposeTimedOut() {
        DockerComposeResult result = dockerComposeClient.dockerComposeCmd()
                .withTimeout(Duration.ofMillis(200))
                .withFile(testUpComposeFile)
                .up();
        assertTrue(result.wasInterrupted());
    }
}
