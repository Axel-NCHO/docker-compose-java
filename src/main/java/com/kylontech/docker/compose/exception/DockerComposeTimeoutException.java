package com.kylontech.docker.compose.exception;

import com.kylontech.docker.compose.DockerComposeResult;
import org.jetbrains.annotations.NotNull;


/**
 * Exception raised when docker compose runs longer than the given timeout. */
public class DockerComposeTimeoutException extends DockerComposeException {

    public DockerComposeTimeoutException(
            @NotNull String message,
            @NotNull DockerComposeResult result
    ) {
        super(message, result);
    }
}
