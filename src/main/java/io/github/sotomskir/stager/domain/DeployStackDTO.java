package io.github.sotomskir.stager.domain;

import java.io.Serializable;
import java.util.Map;

public class DeployStackDTO implements Serializable {
    private String name;
    private String owner;
    private Template template;
    private Map<String, String> environment;

    public DeployStackDTO() {
    }

    public DeployStackDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }
}
