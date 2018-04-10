package io.github.sotomskir.stager.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing docker templates.
 */
@RestController
@RequestMapping("/api")
public class TemplateResource {

    private final Logger log = LoggerFactory.getLogger(TemplateResource.class);

    private final TemplateService templateService;

    public TemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * GET /templates : get all templates.
     *
     * @return the ResponseEntity with status 200 (OK) and with body all templates
     */
    @GetMapping("/templates")
    @Timed
    public ResponseEntity<String> getAllTemplates() {
        log.debug("REST request to get all Templates");
        final String templates = templateService.getAllTemplates();
        return new ResponseEntity<>(templates, HttpStatus.OK);
    }

    /**
     * GET /templates/:name : get the "name" stack.
     *
     * @param name the name of the stack to find
     * @return the ResponseEntity with status 200 (OK) and with body the "name" stack, or with status 404 (Not Found)
     */
    @GetMapping("/templates/{name}")
    @Timed
    public ResponseEntity<Template> getTemplate(@PathVariable String name) {
        log.debug("REST request to get Template : {}", name);
        return ResponseUtil.wrapOrNotFound(templateService.getTemplateByName(name));
    }
}
