package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

class DockerComposeClientImpl implements DockerComposeClient {

    @Override
    @NotNull
    public DockerComposeCmd.Exec dockerComposeCmd() {
        return new DockerComposeCmdImpl();
    }
}
