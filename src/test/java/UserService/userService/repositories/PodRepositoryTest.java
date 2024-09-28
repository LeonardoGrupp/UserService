package UserService.userService.repositories;

import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import UserService.userService.vo.Pod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PodRepositoryTest {

    @Autowired
    private PodRepository podRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findByUrlIgnoreCaseShouldReturnPod() {
        Pod pod = new Pod("type", "title", "url", "release");
        podRepository.save(pod);

        Optional<Pod> response = podRepository.findByUrlIgnoreCase("url");

        assertEquals(pod, response.get(), "ERROR: was not identical");
    }

    @Test
    void existsByUrlIgnoreCaseTrue() {
        Pod pod = new Pod("type", "title", "url", "release");
        podRepository.save(pod);

        boolean isTrue = podRepository.existsByUrlIgnoreCase("url");

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void existsByUrlIgnoreCaseFalse() {
        boolean isFalse = podRepository.existsByUrlIgnoreCase("url");

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void findPodsByGenresReturnsList() {
        Genre business = new Genre("Business");
        genreRepository.save(business);
        List<Genre> genres = Arrays.asList(business);
        Pod pod = new Pod("type", "title", "url", "release");
        pod.setGenres(genres);

        podRepository.save(pod);

        List<Pod> response = podRepository.findPodsByGenres(business);

        assertEquals(1, response.size(), "ERROR: Sizes was not identical");
        assertEquals("Business", response.get(0).getGenres().get(0).getGenre(), "ERROR: Business was not the genre");
        assertEquals(pod, response.get(0), "ERROR: Pod was not identical");
    }
}