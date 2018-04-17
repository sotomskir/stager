package io.github.sotomskir.stager.service;

import io.github.sotomskir.stager.config.ApplicationProperties;
import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TemplateService {
    private final ApplicationProperties applicationProperties;
    private RestTemplate rest;

    private final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository, ApplicationProperties applicationProperties, RestTemplate rest) {
        this.rest = rest;
        this.templateRepository = templateRepository;
        this.applicationProperties = applicationProperties;
    }

    public String getAllTemplates() {
        return rest.getForObject(applicationProperties.getTemplates().getUri() + "/templates.json", String.class);
    }

    public Optional<Template> getTemplateByName(String name) {
        return templateRepository.findOneByName(name);
    }
}
