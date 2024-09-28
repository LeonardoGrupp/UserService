package UserService.userService.controllers;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.services.PlayedMediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayedMediaControllerTest {
    private PlayedMediaService playedMediaServiceMock;
    private PlayedMediaController playedMediaController;

    @BeforeEach
    void setUp() {
        playedMediaServiceMock = mock(PlayedMediaService.class);
        playedMediaController = new PlayedMediaController(playedMediaServiceMock);
    }

    @Test
    void allPlayedMediaShouldReturnList() {
        List<PlayedMedia> playedMediaList = Arrays.asList(
                new PlayedMedia("music", "title", "url", "release"),
                new PlayedMedia("music", "title2", "url2", "release2"),
                new PlayedMedia("music", "title3", "url3", "release3")
        );

        when(playedMediaServiceMock.allPlayedMedia()).thenReturn(playedMediaList);

        ResponseEntity<List<PlayedMedia>> response = playedMediaController.allPlayedMedia();

        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals("title2", response.getBody().get(1).getTitle(), "ERROR: Titles was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedMediaServiceMock).allPlayedMedia();
    }

    @Test
    void createShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");

        when(playedMediaServiceMock.create(playedMedia)).thenReturn(playedMedia);

        ResponseEntity<PlayedMedia> response = playedMediaController.create(playedMedia);

        assertEquals("music", response.getBody().getType(), "ERROR: Types was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedMediaServiceMock).create(playedMedia);
    }

    @Test
    void deleteShouldReturnString() {
        String expected = "Deleted PlayedMedia item";
        long userId = 1;

        when(playedMediaServiceMock.delete(userId)).thenReturn(expected);

        ResponseEntity<String> response = playedMediaController.delete(userId);

        assertEquals(expected, response.getBody(), "ERROR: Strings was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedMediaServiceMock).delete(userId);
    }
}