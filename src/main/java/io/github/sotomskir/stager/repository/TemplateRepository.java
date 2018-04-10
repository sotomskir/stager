package io.github.sotomskir.stager.repository;

import io.github.sotomskir.stager.domain.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository {

    List<Template> findAll();

    Optional<Template> findOneByName(String name);
}
