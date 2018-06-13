package io.github.sotomskir.stager.service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import io.github.sotomskir.stager.StagerApp;
import io.github.sotomskir.stager.config.ApplicationProperties;
import io.github.sotomskir.stager.domain.DeployStackDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;

/**
 * Test class for the UserResource REST controller.
 *
 * @see DockerService
 */
@RunWith(SpringRunner.class)
public class DockerServiceTest {


    @TestConfiguration
    static class DockerClientContextConfiguration {
        @MockBean
        private DockerClient dockerClient;

        @MockBean
        private ApplicationProperties properties;

        @Bean
        public DockerService employeeService() {
            return new DockerService(dockerClient, properties);
        }
    }

    @Autowired
    DockerService dockerService;


    @Before
    public void init() {
    }

    @Test
    @Transactional
    public void assertThatUserMustExistToResetPassword() throws DockerException, InterruptedException {
        dockerService.getAllStacks();
    }

    @Test
    public void yaml() {
        File f = new File("/home/sotomskir/webapps/stager/docker-compose.yml");
        DeployStackDTO stack = new DeployStackDTO();
        dockerService.addServiceLabels(f, stack);
    }
}
