package io.github.sotomskir.stager.service;

import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TemplateService {
    private RestTemplate rest;

    private final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.rest = new RestTemplate();
        this.templateRepository = templateRepository;
    }

    public String getAllTemplates() {
        return rest.getForObject("http://localhost:8765/templates.json", String.class);
    }

    public Optional<Template> getTemplateByName(String name) {
        return templateRepository.findOneByName(name);
    }
}
