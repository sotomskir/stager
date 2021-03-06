package io.github.sotomskir.stager.domain;

import com.spotify.docker.client.messages.swarm.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stack implements Serializable {
    private String name;
    private String owner;
    private Template template;
    private Map<String, String> environment;
    private List<Service> services = new ArrayList<>();

    public Stack() {
    }

    public Stack(String name) {
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

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }
}
