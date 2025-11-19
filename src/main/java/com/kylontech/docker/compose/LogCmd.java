package com.kylontech.docker.compose;


import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * docker compose log */
public class LogCmd extends DockerComposeCmdImpl<LogCmd> {

    @Override
    protected void buildSubCommand(@NotNull List<@NotNull String> cmdList) {
        cmdList.add("log");
    }
}
