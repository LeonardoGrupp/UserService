package UserService.userService.controllers;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.services.PlayedGenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayedGenreControllerTest {

    private PlayedGenreService playedGenreServiceMock;
    private PlayedGenreController playedGenreController;

    @BeforeEach
    void setUp() {
        playedGenreServiceMock = mock(PlayedGenreService.class);
        playedGenreController = new PlayedGenreController(playedGenreServiceMock);
    }

    @Test
    void allPlayedGenresShouldReturnList() {
        List<PlayedGenre> playedGenreList = Arrays.asList(
                new PlayedGenre("rock", "music"),
                new PlayedGenre("jazz", "music"),
                new PlayedGenre("hip-hop", "music")
        );

        when(playedGenreServiceMock.allPlayedGenres()).thenReturn(playedGenreList);

        ResponseEntity<List<PlayedGenre>> response = playedGenreController.allPlayedGenres();

        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals("rock", response.getBody().get(0).getGenre(), "ERROR: Genre was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedGenreServiceMock).allPlayedGenres();
    }

    @Test
    void createShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");

        when(playedGenreServiceMock.create(playedGenre)).thenReturn(playedGenre);

        ResponseEntity<PlayedGenre> response = playedGenreController.create(playedGenre);

        assertEquals(playedGenre, response.getBody(), "ERROR: PlayedGenres was not identical");
        assertEquals("rock", response.getBody().getGenre(), "ERROR: Genres was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedGenreServiceMock).create(playedGenre);
    }

    @Test
    void deleteShouldReturnString() {
        String expected = "PlayedGenre deleted";
        long userId = 1;

        when(playedGenreServiceMock.delete(userId)).thenReturn(expected);

        ResponseEntity<String> response = playedGenreController.delete(userId);

        assertEquals("PlayedGenre deleted", response.getBody().toString(), "ERROR: String was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedGenreServiceMock).delete(userId);
    }
}