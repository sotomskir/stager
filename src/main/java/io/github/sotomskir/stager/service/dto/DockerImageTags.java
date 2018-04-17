package io.github.sotomskir.stager.service.dto;

import java.util.List;

public class DockerImageTags {
    private String name;
    private List<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
