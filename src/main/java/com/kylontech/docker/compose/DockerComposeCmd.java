package com.kylontech.docker.compose;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.List;

public interface DockerComposeCmd {

    /**
     * Builder interface for constructing docker compose commands
     */
    interface Exec {

        /**
         * Specify the compose file to use
         * @param file the compose file
         * @return this builder for chaining
         */
        @NotNull
        Exec withFile(@NotNull File file);

        /**
         * Specify multiple compose files
         * @param files list of compose files
         * @return this builder for chaining
         */
        @NotNull
        Exec withFiles(@NotNull List<@NotNull File> files);

        /**
         * Specify the project name
         * @param projectName the project name
         * @return this builder for chaining
         */
        @NotNull
        Exec withProjectName(@NotNull String projectName);

        /**
         * Run in detached mode
         * @param detached true for detached mode
         * @return this builder for chaining
         */
        @NotNull
        Exec withDetached(boolean detached);

        /**
         * Set environment variables
         * @param key environment variable name
         * @param value environment variable value
         * @return this builder for chaining
         */
        @NotNull
        Exec withEnv(@NotNull String key, @NotNull String value);

        /**
         * Set the working directory
         * @param workingDir the working directory
         * @return this builder for chaining
         */
        @NotNull
        Exec withWorkingDirectory(@NotNull File workingDir);

        /**
         * Set a timeout for the command execution
         * @param timeout the timeout duration
         * @return this builder for chaining
         */
        @NotNull
        Exec withTimeout(@NotNull Duration timeout);

        /**
         * Execute 'docker compose up' command
         * @return result of the command execution
         */
        @NotNull
        DockerComposeResult up();

        /**
         * Execute 'docker compose down' command
         * @return result of the command execution
         */
        @NotNull
        DockerComposeResult down();

        /**
         * Execute 'docker compose ps' command
         * @return result of the command execution
         */
        @NotNull
        DockerComposeResult ps();

        /**
         * Execute 'docker compose logs' command
         * @return result of the command execution
         */
        @NotNull
        DockerComposeResult logs();

        /**
         * Execute a custom docker compose command
         * @param command the command to execute (e.g., "up", "down")
         * @param args additional arguments
         * @return result of the command execution
         */
        @NotNull
        DockerComposeResult exec(@NotNull String command, @NotNull String... args);
    }
}
