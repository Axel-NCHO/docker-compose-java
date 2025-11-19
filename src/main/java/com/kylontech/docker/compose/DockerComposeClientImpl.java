package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

class DockerComposeClientImpl implements DockerComposeClient {

    @Override
    @NotNull
    public UpCmd upCmd() {
        return new UpCmd();
    }

    @Override
    public @NotNull DownCmd downCmd() {
        return new DownCmd();
    }

    @Override
    public @NotNull LogCmd logCmd() {
        return new LogCmd();
    }

    @Override
    public @NotNull PsCmd psCmd() {
        return new PsCmd();
    }
}
