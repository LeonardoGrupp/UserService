package UserService.userService.controllers;

import UserService.userService.entites.User;
import UserService.userService.services.PodService;
import UserService.userService.services.UserService;
import UserService.userService.vo.Pod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PodControllerTest {
    private PodService podServiceMock;
    private UserService userServiceMock;
    private PodController podController;

    @BeforeEach
    void setUp() {
        podServiceMock = mock(PodService.class);
        userServiceMock = mock(UserService.class);
        podController = new PodController(podServiceMock, userServiceMock);
    }

    @Test
    void allPodsShouldReturnList() {
        List<Pod> allPods = Arrays.asList(
                new Pod("pod", "title", "url", "release"),
                new Pod("pod", "title2", "url2", "release2"),
                new Pod("pod", "title3", "url3", "release3")
        );

        when(podServiceMock.findAllPods()).thenReturn(allPods);

        ResponseEntity<List<Pod>> response = podController.allPods();

        assertEquals("url2", response.getBody().get(1).getUrl(), "ERROR: URL was not identical");
        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void podRecommendationsShouldReturnList() {
        List<Pod> podRecommendationList = Arrays.asList(
                new Pod("pod", "title", "url", "release"),
                new Pod("pod", "title2", "url2", "release2"),
                new Pod("pod", "title3", "url3", "release3")
        );

        long userId = 1;

        User user = new User("Freddan");
        user.setId(userId);

        when(userServiceMock.podRecommendations(userId)).thenReturn(podRecommendationList);

        ResponseEntity<List<Pod>> response = podController.podRecommendations(userId);

        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals("title3", response.getBody().get(2).getTitle(), "ERROR: TItles was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was identical");

        verify(userServiceMock).podRecommendations(userId);
    }
}