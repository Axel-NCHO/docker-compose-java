package com.kylontech.docker.compose.exception;

/**
 * Exception raised when docker compose is not installed on the host. */
public class DockerComposeBinaryNotFoundException extends DockerComposeException {
    public DockerComposeBinaryNotFoundException(String message) {
        super(message);
    }
}
