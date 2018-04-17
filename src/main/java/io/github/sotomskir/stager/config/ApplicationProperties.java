package io.github.sotomskir.stager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Stager.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Docker docker = new Docker();
    private final Nexus nexus = new Nexus();
    private final Templates templates = new Templates();

    public Docker getDocker() {
        return docker;
    }

    public Nexus getNexus() {
        return nexus;
    }

    public Templates getTemplates() {
        return templates;
    }

    public static class Docker {
        private String uri;
        private String proxyDomain;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getProxyDomain() {
            return proxyDomain;
        }

        public void setProxyDomain(String proxyDomain) {
            this.proxyDomain = proxyDomain;
        }
    }

    public static class Nexus {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Templates {
        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
