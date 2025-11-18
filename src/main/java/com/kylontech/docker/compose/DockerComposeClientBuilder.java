package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public class DockerComposeClientBuilder {

    @NotNull
    private static final DockerComposeClientBuilder INSTANCE = new DockerComposeClientBuilder();

    private DockerComposeClientBuilder() {}

    /**
     * Creates a default implementation of DockerCompose
     * @return a new DockerCompose instance
     */
    @NotNull
    static DockerComposeClientBuilder getInstance() {
        return INSTANCE;
    }

    @NotNull
    public DockerComposeClient build() {
        return new DockerComposeClientImpl();
    }
}
