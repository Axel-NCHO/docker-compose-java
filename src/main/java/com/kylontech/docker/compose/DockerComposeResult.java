package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public record DockerComposeResult(int exitCode, String stdout, String stderr, String executedCommand) {

    public boolean success() {
        return exitCode == 0;
    }
    public boolean interrupted() {
        return exitCode == 130;
    }

    @Override
    @NotNull
    public String toString() {
        return "DockerComposeResult{" +
                "exitCode=" + exitCode +
                ", command='" + executedCommand + '\'' +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                '}';
    }
}
