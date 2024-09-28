package UserService.userService.repositories;

import UserService.userService.vo.Video;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VideoRepositoryTest {

    @Autowired
    private VideoRepository videoRepository;

    // optional video
    @Test
    void findByUrlIgnoreCaseShouldReturnVideo() {
        Video video = new Video("video", "title", "url", "release");
        videoRepository.save(video);

        Optional<Video> response = videoRepository.findByUrlIgnoreCase("url");

        assertEquals(video, response.get(), "ERROR: videos was not identical");
    }

    // boolean
    @Test
    void existsByUrlIgnoreCaseShouldReturnTrue() {
        Video video = new Video("type", "title", "url", "release");
        videoRepository.save(video);

        boolean isTrue = videoRepository.existsByUrlIgnoreCase("url");

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void existsByUrlIgnoreCaseShouldReturnFalse() {
        boolean isFalse = videoRepository.existsByUrlIgnoreCase("url");

        assertFalse(isFalse, "ERROR: was True");
    }
}