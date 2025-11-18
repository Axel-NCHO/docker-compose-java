package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public interface DockerComposeClient {

    /**
     * Creates a new DockerComposeCmd builder for executing docker-compose commands
     * @return a new command builder instance
     */
    @NotNull
    DockerComposeCmd.Exec dockerComposeCmd();
}