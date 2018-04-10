package io.github.sotomskir.stager.web.rest.errors;

public class DockerClientException extends Exception {
    public DockerClientException() {
        super();
    }

    public DockerClientException(String message) {
        super(message);
    }
}
