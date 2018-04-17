package io.github.sotomskir.stager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Service;
import io.github.sotomskir.stager.domain.DeployStackDTO;
import io.github.sotomskir.stager.domain.Stack;
import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.service.DockerService;
import io.github.sotomskir.stager.web.rest.errors.DockerClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing docker deployed stacks.
 */
@RestController
@RequestMapping("/api/docker")
public class DockerResource {

    private final Logger log = LoggerFactory.getLogger(DockerResource.class);

    private final DockerService dockerService;

    public DockerResource(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    /**
     * GET all stacks.
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @GetMapping("/stacks")
    @Timed
    public ResponseEntity<List<Stack>> getStacks() throws DockerException, InterruptedException {
        log.debug("REST request to get all docker Stacks");
        return ResponseEntity.ok(dockerService.getAllStacks());
    }

    /**
     * GET all services.
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @GetMapping("/services")
    @Timed
    public ResponseEntity<List<Service>> getServices() throws DockerException, InterruptedException {
        log.debug("REST request to get all docker Services");
        return ResponseEntity.ok(dockerService.getAllServices());
    }

    @PostMapping("/stacks")
    @Timed
    public ResponseEntity deployStack(@RequestBody DeployStackDTO stack) throws IOException, InterruptedException, DockerClientException, DockerException {
        log.debug("REST request to deploy docker Stack");
        dockerService.deploy(stack);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stacks/{name}")
    @Timed
    public ResponseEntity deleteStack(@PathVariable String name) throws IOException, InterruptedException, DockerClientException, DockerException {
        log.debug("REST request to delete docker Stack {}", name);
        dockerService.deleteStack(name);
        return ResponseEntity.noContent().build();
    }
}
