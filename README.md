# Docker Compose Java client

This package provides a small and focused Java wrapper around the Docker Compose CLI. It exposes a minimal command API and simple configuration options without introducing external dependencies or abstractions beyond the underlying CLI.

## Features

Supports the core Compose commands: `up`, `down`, `ps`, `log`.

Supports common options: `--file`, `--project-name`, `-d`.

Supports per-command environment variables. This does not load `.env` files automatically.

Supports a working directory override.

Supports a timeout mechanism to stop processes that overrun a given duration.

## Basic usage

Create a client through the builder.

```java
DockerComposeClient client = DockerComposeClientBuilder.getInstance().build();

DockerComposeResult result = client.dockerComposeCmd()
    .withDetached(true)
    .up();

if (result.success()) {
    // containers started
}
```

## Selecting a Compose file

```java
DockerComposeResult result = client.dockerComposeCmd()
    .withFile(new File("docker-compose.yml"))
    .up();
```

Using both a file and a working directory is supported.

## Setting environment variables

```java
DockerComposeResult result = client.dockerComposeCmd()
    .withFile(new File("docker-compose.yml"))
    .withEnv("TEST_VAR", "value")
    .up();
```

Variables are passed directly to the Compose process.

## Timeouts

A timeout stops the Compose process if it exceeds the given duration and marks the result as interrupted.

```java
DockerComposeResult result = client.dockerComposeCmd()
    .withFile(new File("docker-compose.yml"))
    .withTimeout(Duration.ofMillis(200))
    .withDetached(false)  // optional, this is the default.
    .up();

if (result.wasInterrupted()) {
    // timed out
}
```

## Notes

* This library shells out to the local Docker Compose binary and relies on the user environment. It does not embed Compose or implement the Compose specification.  
* This library currently supports only a subset of the Compose API (see [supported commands and options](https://github.com/Axel-NCHO/docker-compose-java?tab=readme-ov-file#features)). 
