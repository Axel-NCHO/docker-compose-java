package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * docker compose down */
public class DownCmd extends DockerComposeCmdImpl<DownCmd> {

    @Override
    protected void buildSubCommand(final @NotNull List<@NotNull String> cmdList) {
        cmdList.add("down");
    }
}
