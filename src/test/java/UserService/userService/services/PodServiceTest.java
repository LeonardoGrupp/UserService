package UserService.userService.services;

import UserService.userService.repositories.PodRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Pod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PodServiceTest {
    private PodRepository podRepositoryMock;
    private PodService podService;

    @BeforeEach
    void setUp() {
        podRepositoryMock = mock(PodRepository.class);
        podService = new PodService(podRepositoryMock);
    }

    @Test
    void findAllPodsShouldReturnList() {
        List<Pod> allPods = Arrays.asList(
                new Pod("pod", "title", "url", "release"),
                new Pod("pod", "title2", "url2", "release2"),
                new Pod("pod", "title3", "url3", "release3")
        );

        when(podRepositoryMock.findAll()).thenReturn(allPods);

        List<Pod> response = podService.findAllPods();

        assertEquals(allPods, response, "ERROR: Lists was not identical");

        verify(podRepositoryMock).findAll();
    }

    @Test
    void podExistsByUrlShouldReturnTrue() {
        when(podRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(true);

        boolean isTrue = podService.podExistsByUrl("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(podRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void podExistsByUrlShouldReturnFalse() {
        when(podRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(false);

        boolean isFalse = podService.podExistsByUrl("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(podRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void findPodByUrlShouldReturnPod() {
        Pod pod = new Pod("pod", "title", "url", "release");

        when(podRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.of(pod));

        Pod result = podService.findPodByUrl("url");

        assertEquals(pod, result, "ERROR: Objects was not identical");

        verify(podRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void findPodByUrlShouldReturnException() {
//        NOT_FOUND, "ERROR: not found"
        when(podRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            podService.findPodByUrl("url");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(podRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void findAllPodsInGenreShouldReturnList() {
        List<Pod> businessPods = Arrays.asList(
                new Pod("pod", "title", "url", "release"),
                new Pod("pod", "title2", "url2", "release2"),
                new Pod("pod", "title3", "url3", "release3")
        );

        Genre genre = new Genre("business", "pod");

        when(podRepositoryMock.findPodsByGenres(genre)).thenReturn(businessPods);

        List<Pod> result = podService.findAllPodsInGenre(genre);

        assertEquals(businessPods, result, "ERROR: Lists was not identical");

        verify(podRepositoryMock).findPodsByGenres(genre);
    }

    @Test
    void addPlayShouldReturnPod() {
        Pod pod = new Pod("pod", "title", "url", "release");

        when(podRepositoryMock.save(pod)).thenReturn(pod);

        Pod result = podService.addPlay(pod);

        assertEquals(1, result.getPlayCounter(), "ERROR: PlayCounter was not 1");
        assertEquals(pod, result, "ERROR: Objects was not identical");

        verify(podRepositoryMock).save(pod);
    }
}