package io.github.sotomskir.stager.service;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Service;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import io.github.sotomskir.stager.config.Constants;
import io.github.sotomskir.stager.domain.Stack;
import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.repository.TemplateRepository;
import io.github.sotomskir.stager.service.util.StreamGobbler;
import io.github.sotomskir.stager.web.rest.errors.DockerClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@org.springframework.stereotype.Service
@Transactional
public class DockerService {
    private final Logger log = LoggerFactory.getLogger(DockerService.class);
    private final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    private final TemplateRepository templateRepository;

    public DockerService(TemplateRepository repository) {
        this.templateRepository = repository;
    }

    public void deploy(Stack stack) throws IOException, InterruptedException, DockerClientException, DockerException {
        File temp = File.createTempFile("docker-compose", ".yml");
        URL url = new URL(stack.getTemplate().getRepository().getUrl() + "/" + stack.getTemplate().getRepository().getStackfile());
        downloadFromUrl(url, temp.getAbsolutePath());
        ProcessBuilder builder = new ProcessBuilder();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(temp))) {
            out.write(stack.getTemplate().getContent());
        } catch (IOException e) {
            log.error("cannot write file: {}, exception: {}", temp.getAbsolutePath(), e.getMessage());
            temp.delete();
            throw new DockerClientException();
        }
        builder.environment();
        builder.command("docker", "stack", "deploy", "-c", temp.getAbsolutePath(), stack.getName());
        stack.getEnvironment().forEach((k, v) -> builder.environment().put(k, v));
        Process process = builder.start();
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), stdout::append);
        StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), stderr::append);
        Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
        Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String message = stderr.toString();
            log.error("stack deploy error: {}", message);
            throw new DockerClientException(message);
        }
        addServiceLabels(stack);
        temp.delete();
        log.info("stack {} deployed successfuly: {}", stack.getName(), stdout.toString());
    }

    void downloadFromUrl(URL url, String localFilename) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URLConnection urlConn = url.openConnection();//connect

            is = urlConn.getInputStream();               //get connection inputstream
            fos = new FileOutputStream(localFilename);   //open outputstream to local file

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private void addServiceLabels(Stack stack) throws DockerException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        docker.listServices().stream()
            .filter(filterByStackName(stack.getName()))
            .forEach(s -> {
                ServiceSpec spec = ServiceSpec.builder()
                    .taskTemplate(s.spec().taskTemplate())
                    .labels(s.spec().labels())
                    .endpointSpec(s.spec().endpointSpec())
                    .mode(s.spec().mode())
                    .name(s.spec().name())
                    .networks(s.spec().networks())
                    .updateConfig(s.spec().updateConfig())
                    .addLabel(
                        Constants.SERVICE_OWNER_LABEL,
                        username
                    ).addLabel(
                        Constants.TEMPLATE_REPOSITORY_STACKFILE_LABEL,
                        stack.getTemplate().getRepository().getStackfile()
                    ).addLabel(
                        Constants.TEMPLATE_REPOSITORY_URL_LABEL,
                        stack.getTemplate().getRepository().getUrl()
                    ).build();
                try {
                    docker.updateService(s.id(), s.version().index(), spec);
                } catch (DockerException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
    }

    public List<Stack> getAllStacks() throws DockerException, InterruptedException {
        List<Service> services = docker.listServices();
        List<Stack> stacks = getStacksFromServices(services);
        stacks.forEach(stack -> {
                List<Service> stackServices = services.stream()
                    .filter(filterByStackName(stack.getName()))
                    .collect(Collectors.toList());
                stack.setEnvironment(getEnvironmentFromServices(stackServices));
                stack.setTemplate(getTemplateFromServices(stackServices));
                stack.setOwner(getOwnerFromServices(stackServices));
            }
        );
        return stacks;
    }

    private Template getTemplateFromServices(List<Service> stackServices) {
        List<Template.Repository> repositoryList = stackServices.stream()
            .map(service -> {
                Template.Repository repository = new Template.Repository();
                repository.setUrl(service.spec().labels().get(Constants.TEMPLATE_REPOSITORY_URL_LABEL));
                repository.setStackfile(service.spec().labels().get(Constants.TEMPLATE_REPOSITORY_STACKFILE_LABEL));
                return repository;
            })
            .distinct()
            .collect(Collectors.toList());
        if (repositoryList.size() != 1) {
            throw new IllegalStateException("More than one repository found");
        }
        return new Template(repositoryList.get(0));
    }

    private String getOwnerFromServices(List<Service> stackServices) {
        List<String> ownerList = stackServices.stream()
            .map(service -> service.spec().labels().get(Constants.SERVICE_OWNER_LABEL))
            .distinct()
            .collect(Collectors.toList());
        if (ownerList.size() != 1) {
            throw new IllegalStateException("More than one owner found");
        }
        return ownerList.get(0);
    }

    private Predicate<Service> filterByStackName(String stackName) {
        return service -> stackName.equals(
            Objects.requireNonNull(service.spec().labels())
            .get(Constants.DOCKER_STACK_NAMESPACE_LABEL)
        );
    }

    private Map<String, String> getEnvironmentFromServices(List<Service> services) {
        return services.stream()
            .map(service -> Objects.requireNonNull(service.spec().taskTemplate().containerSpec()).env())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                o -> o.split("=")[0], o -> o.split("=").length > 1 ? o.split("=")[1] : "")
            );
    }

    private List<Stack> getStacksFromServices(List<Service> services) {
        return services.stream()
            .map(service -> Objects.requireNonNull(service.spec().labels()).get(Constants.DOCKER_STACK_NAMESPACE_LABEL))
            .distinct()
            .filter(Objects::nonNull)
            .map(Stack::new)
            .collect(Collectors.toList());
    }
}
