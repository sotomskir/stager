package io.github.sotomskir.stager.service;

import com.spotify.docker.client.exceptions.DockerException;
import io.github.sotomskir.stager.StagerApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the UserResource REST controller.
 *
 * @see DockerService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StagerApp.class)
@Transactional
public class DockerServiceTest {

    @Autowired
    private DockerService dockerService;

    @Before
    public void init() {
    }

    @Test
    @Transactional
    public void assertThatUserMustExistToResetPassword() throws DockerException, InterruptedException {
        dockerService.getAllStacks();
    }
}
