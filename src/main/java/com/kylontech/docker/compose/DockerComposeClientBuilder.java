package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

public class DockerComposeClientBuilder {

    @NotNull
    private static final DockerComposeClientBuilder INSTANCE = new DockerComposeClientBuilder();

    private DockerComposeClientBuilder() {}

    /**
     * Creates a default implementation of DockerComposeClient
     * @return a new DockerComposeClient instance
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
