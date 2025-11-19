package com.kylontech.docker.compose.exception;

import com.kylontech.docker.compose.DockerComposeResult;
import org.jetbrains.annotations.NotNull;

/**
 * Exception raised when docker compose exited with a non-zero error code. */
public class DockerComposeExecutionException extends DockerComposeException {

    public DockerComposeExecutionException(@NotNull String message, @NotNull DockerComposeResult result) {
        super(message, result);
    }

    public DockerComposeExecutionException(
            @NotNull String message,
            @NotNull Throwable cause,
            @NotNull DockerComposeResult result
    ) {
        super(message, cause, result);
    }
}
