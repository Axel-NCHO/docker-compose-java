package com.kylontech.docker.compose;

import com.kylontech.docker.compose.exception.DockerComposeException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.List;

public interface DockerComposeCmd {

    /**
     * Builder interface for constructing docker compose commands
     */
    abstract class Exec<T extends Exec<T>> {

        @SuppressWarnings("unchecked")
        protected final T self() {
            return (T) this;
        }

        /**
         * Specify the compose file to use
         * @param file the compose file
         * @return this builder for chaining
         */
        @NotNull
        abstract T withFile(@NotNull File file);

        /**
         * Specify multiple compose files
         * @param files list of compose files
         * @return this builder for chaining
         */
        @NotNull
        abstract T withFiles(@NotNull List<@NotNull File> files);

        /**
         * Specify the project name
         * @param projectName the project name
         * @return this builder for chaining
         */
        @NotNull
        abstract T withProjectName(@NotNull String projectName);

        /**
         * Set environment variables
         * @param key environment variable name
         * @param value environment variable value
         * @return this builder for chaining
         */
        @NotNull
        abstract T withEnv(@NotNull String key, @NotNull String value);

        /**
         * Set the working directory
         * @param workingDir the working directory
         * @return this builder for chaining
         */
        @NotNull
        abstract T withWorkingDirectory(@NotNull File workingDir);

        /**
         * Set a timeout for the command execution
         * @param timeout the timeout duration
         * @return this builder for chaining
         */
        @NotNull
        abstract T withTimeout(@NotNull Duration timeout);

        abstract DockerComposeResult exec() throws DockerComposeException;
    }


}
