package UserService.userService.controllers;

import UserService.userService.entites.User;
import UserService.userService.services.GenreService;
import UserService.userService.services.MusicService;
import UserService.userService.services.UserService;
import UserService.userService.vo.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MusicControllerTest {
    private MusicService musicServiceMock;
    private UserService userServiceMock;
    private MusicController musicController;

    @BeforeEach
    void setUp() {
        musicServiceMock = mock(MusicService.class);
        userServiceMock = mock(UserService.class);
        musicController = new MusicController(musicServiceMock, userServiceMock);
    }

    @Test
    void allMusicShouldReturnList() {
        List<Music> music = Arrays.asList(
                new Music("music", "title", "url", "release"),
                new Music("music", "title2", "url2", "release2"),
                new Music("music", "title3", "url3", "release3")
        );

        when(musicServiceMock.findAllMusic()).thenReturn(music);

        ResponseEntity<List<Music>> response = musicController.allMusic();

        assertEquals(music,  response.getBody(), "ERROR: Lists was not identical");
        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(musicServiceMock).findAllMusic();
    }

    @Test
    void musicRecommendationsShouldReturnList() {
        long userId = 1;
        User user = new User("Freddan");
        user.setId(userId);

        List<Music> musicRecommendationsList = Arrays.asList(
                new Music("music", "title", "url", "release"),
                new Music("music", "title2", "url2", "release2"),
                new Music("music", "title3", "url3", "release3")
        );

        when(userServiceMock.musicRecommendations(userId)).thenReturn(musicRecommendationsList);

        ResponseEntity<List<Music>> response = musicController.musicRecommendations(userId);

        assertEquals(musicRecommendationsList, response.getBody(), "ERROR: Lists was not identical");
        assertEquals("title", response.getBody().get(0).getTitle(), "ERROR: Titles was not identical");
        assertEquals(3, response.getBody().size(), "ERROR: Sizes of lists was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).musicRecommendations(userId);
    }
}