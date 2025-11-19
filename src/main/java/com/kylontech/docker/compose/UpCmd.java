package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * docker compose up */
public class UpCmd extends DockerComposeCmdImpl<UpCmd> {

    private boolean detached = false;
    @NotNull
    private final List<@NotNull String> args = new ArrayList<>();

    /**
     * Run in detached mode
     * @param detached true for detached mode
     * @return this builder for chaining
     */
    @NotNull
    public UpCmd withDetached(boolean detached) {
        this.detached = detached;
        return this;
    }

    @Override
    protected void buildSubCommand(final @NotNull List<@NotNull String> cmdList) {
        cmdList.add("up");
        if (detached) {
            args.add("-d");
        }
        cmdList.addAll(args);
    }
}
