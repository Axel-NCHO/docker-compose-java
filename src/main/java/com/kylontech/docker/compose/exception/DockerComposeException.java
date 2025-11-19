package com.kylontech.docker.compose.exception;

import com.kylontech.docker.compose.DockerComposeResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Broad exception. Raised for any error when using a docker compose client. */
public class DockerComposeException extends RuntimeException {

    @Nullable
    public final DockerComposeResult result;

    public DockerComposeException(String message) {
        super(message);
        result = null;
    }

    public DockerComposeException(String message, @NotNull DockerComposeResult result) {
        super(message);
        this.result = result;
    }

    public DockerComposeException(String message, Throwable cause, @NotNull DockerComposeResult result) {
        super(message, cause);
        this.result = result;
    }
}
