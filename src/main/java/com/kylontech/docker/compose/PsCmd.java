package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * docker compose ps */
public class PsCmd extends DockerComposeCmdImpl<PsCmd> {

    @Override
    protected void buildSubCommand(final @NotNull List<@NotNull String> cmdList) {
        cmdList.add("ps");
    }
}
