package io.github.sotomskir.stager.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.sotomskir.stager.config.ApplicationProperties;
import io.github.sotomskir.stager.service.dto.DockerImageTags;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class NexusService {
    private final Logger log = LoggerFactory.getLogger(NexusService.class);
    private final RestTemplate rest;
    private final ApplicationProperties applicationProperties;

    public NexusService(RestTemplate rest, ApplicationProperties applicationProperties) {
        this.rest = rest;
        this.applicationProperties = applicationProperties;
    }

    public DockerImageTags getTags(String image) {
        DockerImageTags tags = rest.getForObject(
            String.format("%s/v2/%s/tags/list", applicationProperties.getNexus().getUrl(), image),
            DockerImageTags.class);
        return removeReleasedSnapshots(tags);
    }

    private DockerImageTags removeReleasedSnapshots(DockerImageTags tags)
    {
        tags.setTags(tags.getTags()
            .stream()
            .filter(tag -> !(isSnapshot(tag) && isReleased(tags, tag)))
            .collect(Collectors.toList()));
        return tags;
    }

    private boolean isReleased(DockerImageTags tags, String tag) {
        String releaseTag = StringUtils.substringBefore(tag, "-SNAPSHOT");
        return tags.getTags().contains(releaseTag);
    }

    private boolean isSnapshot(String tag) {
        return tag.contains("-SNAPSHOT");
    }
}
