package com.kylontech.docker.compose.exception;

import com.kylontech.docker.compose.DockerComposeResult;
import org.jetbrains.annotations.NotNull;

public class DockerComposeInterruptedException extends DockerComposeExecutionException {

    public DockerComposeInterruptedException(
            @NotNull String message,
            @NotNull Throwable cause,
            @NotNull DockerComposeResult result
    ) {
        super(message, cause, result);
    }
}
