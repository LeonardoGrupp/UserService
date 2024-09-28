package UserService.userService.controllers;

import UserService.userService.services.UserService;
import UserService.userService.services.VideoService;
import UserService.userService.vo.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoControllerTest {
    private VideoService videoServiceMock;
    private UserService userServiceMock;
    private VideoController videoController;

    @BeforeEach
    void setUp() {
        videoServiceMock = mock(VideoService.class);
        userServiceMock = mock(UserService.class);
        videoController = new VideoController(videoServiceMock, userServiceMock);
    }

    @Test
    void allVideosShouldReturnList() {
        List<Video> videoList = Arrays.asList(
                new Video("video", "title", "url", "release"),
                new Video("video", "title2", "url2", "release2"),
                new Video("video", "title3", "url3", "release3")
        );

        when(videoServiceMock.findAllVideos()).thenReturn(videoList);

        ResponseEntity<List<Video>> response = videoController.allVideos();

        assertEquals(videoList, response.getBody(), "ERROR: Lists was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(videoServiceMock).findAllVideos();
    }

    @Test
    void videoRecommendationsShouldReturnList() {
        List<Video> videoRecommendationList = Arrays.asList(
                new Video("video", "title", "url", "release"),
                new Video("video", "title2", "url2", "release2"),
                new Video("video", "title3", "url3", "release3")
        );
        long userId = 1;

        when(userServiceMock.videoRecommendations(userId)).thenReturn(videoRecommendationList);

        ResponseEntity<List<Video>> response = videoController.videoRecommendations(userId);

        assertEquals(videoRecommendationList, response.getBody(), "ERROR: Lists was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).videoRecommendations(userId);
    }
}