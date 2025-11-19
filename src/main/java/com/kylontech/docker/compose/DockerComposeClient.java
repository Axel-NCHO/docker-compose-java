package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public interface DockerComposeClient {

    /**
     * Creates a 'docker compose up' command builder.
     * @return a new command builder instance
     */
    @NotNull
    UpCmd upCmd();

    /**
     * Creates a 'docker compose down' command builder.
     * @return a new command builder instance
     */
    @NotNull
    DownCmd downCmd();

    /**
     * Creates a 'docker compose log' command builder.
     * @return a new command builder instance
     */
    @NotNull
    LogCmd logCmd();

    /**
     * Creates a 'docker compose ps' command builder.
     * @return a new command builder instance
     */
    @NotNull
    PsCmd psCmd();
}