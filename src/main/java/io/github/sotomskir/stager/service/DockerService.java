package io.github.sotomskir.stager.service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Service;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import io.github.sotomskir.stager.config.ApplicationProperties;
import io.github.sotomskir.stager.config.Constants;
import io.github.sotomskir.stager.domain.DeployStackDTO;
import io.github.sotomskir.stager.domain.Stack;
import io.github.sotomskir.stager.domain.Template;
import io.github.sotomskir.stager.service.util.StreamGobbler;
import io.github.sotomskir.stager.web.rest.errors.DockerClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    private final DockerClient docker;
    private final ApplicationProperties applicationProperties;

    public DockerService(DockerClient docker, ApplicationProperties applicationProperties) {
        this.docker = docker;
        this.applicationProperties = applicationProperties;
    }

    public void deploy(DeployStackDTO stack) throws IOException, InterruptedException, DockerClientException {
        File temp = File.createTempFile("docker-compose", ".yml");
        URL url = new URL(applicationProperties.getTemplates().getUri() + "/" + stack.getTemplate().getRepository().getStackfile());
        downloadFromUrl(url, temp.getAbsolutePath());
        addServiceLabels(temp, stack);
        ProcessBuilder builder = new ProcessBuilder();
        builder.environment();
        builder.command("docker", "stack", "deploy", "--with-registry-auth", "-c", temp.getAbsolutePath(), stack.getName());
        stack.getEnvironment().forEach((k, v) -> builder.environment().put(k, v));
        builder.environment().put("STACK_NAME", stack.getName());
        builder.environment().put("PROXY_DOMAIN", applicationProperties.getDocker().getProxyDomain());
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
        Thread.sleep(1000);
//        addServiceLabels(stack);
//        temp.delete();
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

    private void addServiceLabels(DeployStackDTO stack) throws DockerException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Service> stackServices = docker.listServices().stream()
            .filter(filterByStackName(stack.getName()))
            .collect(Collectors.toList());

        for (Service s : stackServices) {
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
            docker.updateService(s.id(), s.version().index(), spec);
        }
    }

    public void addServiceLabels(File file, DeployStackDTO stack) {
        Map<String, Map> compose = (Map<String, Map>) fromYaml(file);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        compose.get("services").values().stream().forEach((service) -> {
            LinkedHashMap<String, LinkedHashMap> deploy = getOrCreate((LinkedHashMap<String, LinkedHashMap>) service, "deploy");
            LinkedHashMap<String, String> labels = getOrCreate(deploy, "labels");
            labels.put(Constants.TEMPLATE_REPOSITORY_URL_LABEL, stack.getTemplate().getRepository().getUrl());
            labels.put(Constants.TEMPLATE_REPOSITORY_STACKFILE_LABEL, stack.getTemplate().getRepository().getStackfile());
            labels.put(Constants.SERVICE_OWNER_LABEL, username);
        });
        toYaml(compose, file);
    }

    private LinkedHashMap getOrCreate(LinkedHashMap<String, LinkedHashMap> map, String key) {
        LinkedHashMap<String, LinkedHashMap> value = map.get(key);
        if (value == null) {
            map.put(key, new LinkedHashMap<String, Object>());
            value = map.get(key);
        }
        return value;
    }

    public void toYaml(Object object, File file) {
        Yaml yaml = new Yaml();
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        yaml.dump(object, writer);
        log.info(yaml.dump(object));
    }

    static String readFile(String path, Charset encoding)
        throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public Object fromYaml(File file) {

        Object obj = null;
        Yaml yaml = new Yaml();
        try {
            obj = yaml.load(readFile(file.getPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public List<Stack> getAllStacks() throws DockerException, InterruptedException {
        List<Service> services = docker.listServices();
        List<Stack> stacks = getStacksFromServices(services);
        stacks.forEach(stack -> {
                List<Service> stackServices = services.stream()
                    .filter(filterByStackName(stack.getName()))
                    .collect(Collectors.toList());
                stack.setServices(stackServices);
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
        Map<String, String> env = services.stream()
            .map(service -> Objects.requireNonNull(service.spec().taskTemplate().containerSpec()).env())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toMap(
                o -> o.split("=")[0],
                o -> o.split("=").length > 1 ? o.split("=")[1] : "",
                (o1, o2) -> {
                    log.warn("duplicate key found! o1: {}, o2: {}, using value of o1", o1, o2);
                    return o1;
                }
            ));

        Map<String, String> versions = services.stream()
            .collect(Collectors.toMap(this::getVersionKey, this::getVersionValue));
        env.putAll(versions);
        return env;
    }

    private String getVersionValue(Service service) {
        String version = StringUtils.substringAfter(service.spec().taskTemplate().containerSpec().image(), ":");
        if (version == null) {
            version = "latest";
        }
        return version;
    }

    private String getVersionKey(Service o) {
        return StringUtils.substringAfter(Objects.requireNonNull(o.spec().name()), "_").toUpperCase() + "_VERSION";
    }

    private List<Stack> getStacksFromServices(List<Service> services) {
        return services.stream()
            .map(service -> Objects.requireNonNull(service.spec().labels()).get(Constants.DOCKER_STACK_NAMESPACE_LABEL))
            .distinct()
            .filter(Objects::nonNull)
            .map(Stack::new)
            .collect(Collectors.toList());
    }

    public List<Service> getAllServices() throws DockerException, InterruptedException {
        return docker.listServices();
    }

    public void deleteStack(String name) throws IOException, InterruptedException, DockerClientException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Request to delete stack: {} made by user: {}", name, username);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("docker", "stack", "rm", name);
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
            log.error("stack rm error: {}", message);
            throw new DockerClientException(message);
        }
        log.info("stack {} removed successfuly: {}", name, stdout.toString());
    }
}
