package io.github.sotomskir.stager.config;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.RegistryAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class DockerConfiguration {
    private final Logger log = LoggerFactory.getLogger(DockerConfiguration.class);
    private ApplicationProperties applicationProperties;
    public DockerConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    DockerClient dockerClient() {
        DockerClient docker = DefaultDockerClient.builder()
            .uri(URI.create(applicationProperties.getDocker().getUri()))
            .build();
        try {
            docker.version();
        } catch (DockerException | InterruptedException e) {
            log.error("Connection to Docker engine failed." + e.getMessage());
        }
        return docker;
    }
}
