package io.github.sotomskir.stager.domain;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

public class Template {
    @NotNull
    private Repository repository;

    @NotNull
    private String content;
    private Map<String, String> environment;

    public Template() {
    }

    public Template(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public static class Repository {
        private String url;
        private String stackfile;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStackfile() {
            return stackfile;
        }

        public void setStackfile(String stackfile) {
            this.stackfile = stackfile;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Repository that = (Repository) o;
            return Objects.equals(url, that.url) &&
                Objects.equals(stackfile, that.stackfile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, stackfile);
        }
    }
}
