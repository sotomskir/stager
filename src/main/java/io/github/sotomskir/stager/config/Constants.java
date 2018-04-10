package io.github.sotomskir.stager.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String SERVICE_OWNER_LABEL = "io.github.sotomskir.stager.service.owner";
    public static final String TEMPLATE_REPOSITORY_STACKFILE_LABEL = "io.github.sotomskir.stager.template.repository.stackfile";
    public static final String TEMPLATE_REPOSITORY_URL_LABEL = "io.github.sotomskir.stager.template.repository.url";
    public static final String DOCKER_STACK_NAMESPACE_LABEL = "com.docker.stack.namespace";

    private Constants() {
    }
}
