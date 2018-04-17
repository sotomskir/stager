package io.github.sotomskir.stager.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DockerCompose {
    @JsonProperty
    private List<Service> services;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public static class Service {
        @JsonProperty
        private Deploy deploy;

        public Deploy getDeploy() {
            return deploy;
        }

        public void setDeploy(Deploy deploy) {
            this.deploy = deploy;
        }

        public static class Deploy {
            @JsonProperty
            private Map<String, String> labels;

            public Map<String, String> getLabels() {
                return labels;
            }

            public void setLabels(Map<String, String> labels) {
                this.labels = labels;
            }
        }
    }
}
