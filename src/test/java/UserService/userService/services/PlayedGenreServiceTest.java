package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.repositories.PlayedGenreRepository;
import UserService.userService.vo.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayedGenreServiceTest {
    private PlayedGenreRepository playedGenreRepositoryMock;
    private PlayedGenreService playedGenreService;

    @BeforeEach
    void setUp() {
        playedGenreRepositoryMock = mock(PlayedGenreRepository.class);
        playedGenreService = new PlayedGenreService(playedGenreRepositoryMock);
    }

    @Test
    void allPlayedGenresShouldReturnList() {
        List<PlayedGenre> playedGenreList = Arrays.asList(
                new PlayedGenre("rock", "music"),
                new PlayedGenre("jazz", "music"),
                new PlayedGenre("hip-hop", "music")
        );

        when(playedGenreRepositoryMock.findAll()).thenReturn(playedGenreList);

        List<PlayedGenre> response = playedGenreService.allPlayedGenres();

        assertEquals(playedGenreList, response, "ERROR: Lists was not identical");
        assertEquals(3, response.size(), "ERROR: Sizes was not identical");

        verify(playedGenreRepositoryMock).findAll();
    }

    @Test
    void findPlayedGenreByIdShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        long pgId = 1;
        playedGenre.setId(pgId);

        when(playedGenreRepositoryMock.findById(pgId)).thenReturn(Optional.of(playedGenre));

        PlayedGenre response = playedGenreService.findPlayedGenreById(pgId);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).findById(pgId);
    }

    @Test
    void findPlayedGenreByIdShouldReturnException() {
        long pgId = 1;

        when(playedGenreRepositoryMock.findById(pgId)).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            playedGenreService.findPlayedGenreById(pgId);
        }, "ERROR: Exceptions was not thrown");

        assertEquals("ERROR: PlayedGenre ID not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedGenreRepositoryMock).findById(pgId);
    }

    @Test
    void createShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");

        when(playedGenreRepositoryMock.save(playedGenre)).thenReturn(playedGenre);

        PlayedGenre response = playedGenreService.create(playedGenre);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).save(playedGenre);
    }

    @Test
    void createFromMusicGenres() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        playedGenreRepositoryMock.save(playedGenre);

        Genre genre = new Genre("rock", "music");

        when(playedGenreRepositoryMock.save(any(PlayedGenre.class))).thenReturn(playedGenre);

        PlayedGenre response = playedGenreService.createFromMusicGenres(genre);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).save(playedGenre);
    }

    @Test
    void createFromPodGenres() {
        PlayedGenre playedGenre = new PlayedGenre("business", "pod");
        playedGenreRepositoryMock.save(playedGenre);

        Genre genre = new Genre("business", "pod");

        when(playedGenreRepositoryMock.save(any(PlayedGenre.class))).thenReturn(playedGenre);

        PlayedGenre response = playedGenreService.createFromPodGenres(genre);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).save(playedGenre);
    }

    @Test
    void createFromVideoGenres() {
        PlayedGenre playedGenre = new PlayedGenre("action", "video");
        playedGenreRepositoryMock.save(playedGenre);

        Genre genre = new Genre("action", "video");

        when(playedGenreRepositoryMock.save(any(PlayedGenre.class))).thenReturn(playedGenre);

        PlayedGenre response = playedGenreService.createFromVideoGenres(genre);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).save(playedGenre);
    }

    @Test
    void saveShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");

        when(playedGenreRepositoryMock.save(playedGenre)).thenReturn(playedGenre);

        PlayedGenre response = playedGenreService.save(playedGenre);

        assertEquals(playedGenre, response, "ERROR: Objects was not identical");

        verify(playedGenreRepositoryMock).save(playedGenre);
    }

    @Test
    void deleteShouldReturnString() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        long id = 1;
        playedGenre.setId(id);

        when(playedGenreRepositoryMock.findById(id)).thenReturn(Optional.of(playedGenre));

        String response = playedGenreService.delete(id);

        assertEquals("PlayedGenre deleted", response, "ERROR: Strings was not identical");

        verify(playedGenreRepositoryMock).delete(playedGenre);
    }

    @Test
    void deleteShouldReturnException() {
        long id = 1;

        when(playedGenreRepositoryMock.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            playedGenreService.delete(id);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: PlayedGenre ID not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(playedGenreRepositoryMock).findById(id);
        verify(playedGenreRepositoryMock, never()).delete(any());
    }
}