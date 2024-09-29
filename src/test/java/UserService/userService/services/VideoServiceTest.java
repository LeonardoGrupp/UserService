package UserService.userService.services;

import UserService.userService.repositories.VideoRepository;
import UserService.userService.vo.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoServiceTest {
    private VideoRepository videoRepositoryMock;
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        videoRepositoryMock = mock(VideoRepository.class);
        videoService = new VideoService(videoRepositoryMock);
    }

    @Test
    void findAllVideosShouldReturnList() {
        List<Video> allVideos = Arrays.asList(
                new Video("video", "title", "url", "release"),
                new Video("video", "title2", "url2", "release2"),
                new Video("video", "title3", "url3", "release3")
        );

        when(videoRepositoryMock.findAll()).thenReturn(allVideos);

        List<Video> response = videoService.findAllVideos();

        assertEquals(allVideos, response, "ERROR: Lists was not identical");

        verify(videoRepositoryMock).findAll();
    }

    @Test
    void videoExistsByUrlShouldReturnTrue() {
        when(videoRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(true);

        boolean isTrue = videoService.videoExistsByUrl("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(videoRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void videoExistsByUrlShouldReturnFalse() {
        when(videoRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(false);

        boolean isFalse = videoService.videoExistsByUrl("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(videoRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void findVideoByUrlShouldReturnVideo() {
        Video video = new Video("video", "title", "url", "release");

        when(videoRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.of(video));

        Video response = videoService.findVideoByUrl("url");

        assertEquals(video, response, "ERROR: Objects was not identical");

        verify(videoRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void findVideoByUrlShouldReturnException() {
        when(videoRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            videoService.findVideoByUrl("url");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(videoRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void addPlayShouldReturnVideo() {
        Video video = new Video("video", "title", "url", "release");

        when(videoRepositoryMock.save(video)).thenReturn(video);

        Video response = videoService.addPlay(video);

        assertEquals(1, response.getPlayCounter(), "ERROR: PlayCounter was not 1");

        verify(videoRepositoryMock).save(video);
    }
}