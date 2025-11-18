package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public record DockerComposeResult(int exitCode, String output, boolean success, String executedCommand) {

    public boolean wasInterrupted() {
        return exitCode == 124;
    }

    @Override
    @NotNull
    public String toString() {
        return "DockerComposeResult{" +
                "exitCode=" + exitCode +
                ", success=" + success +
                ", command='" + executedCommand + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
