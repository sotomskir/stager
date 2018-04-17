package io.github.sotomskir.stager.web.rest;

import io.github.sotomskir.stager.service.NexusService;
import io.github.sotomskir.stager.service.dto.DockerImageTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nexus/v2")
public class NexusResource {

    private final Logger log = LoggerFactory.getLogger(NexusResource.class);

    private final NexusService nexusService;

    public NexusResource(NexusService nexusService) {
        this.nexusService = nexusService;
    }

    @GetMapping("{image}/tags/list")
    public ResponseEntity<DockerImageTags> getVersions(
        @PathVariable("image") String image) {
        log.debug("REST request to get versions of {}", image);
        return ResponseEntity.ok(nexusService.getTags(image));
    }
}
