package io.github.sotomskir.stager.repository;

import io.github.sotomskir.stager.domain.Template;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MockTemplateRepository implements TemplateRepository {
    @Override
    public List<Template> findAll() {
        List<Template> templates = new ArrayList<>();
        templates.add(new Template());
        templates.add(new Template());
        templates.add(new Template());
        templates.add(new Template());
        return templates;
    }

    @Override
    public Optional<Template> findOneByName(String name) {
        Template template = new Template();
        Map<String, String> environment = new HashMap<>();
        environment.put("psql_version", "9.6");
        environment.put("service_version", "1.0");
        environment.put("djdidaj_version", "1.2");
        template.setEnvironment(environment);
        return Optional.of(template);
    }
}
