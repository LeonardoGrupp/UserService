package UserService.userService.services;

import UserService.userService.repositories.GenreRepository;
import UserService.userService.vo.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceTest {
    private GenreService genreService;
    private GenreRepository genreRepositoryMock;

    @BeforeEach
    void setUp() {
        genreRepositoryMock = mock(GenreRepository.class);
        genreService = new GenreService(genreRepositoryMock);
    }

    @Test
    void getAllGenresShouldReturnList() {
        List<Genre> genreList = Arrays.asList(
                new Genre("rock"),
                new Genre("jazz")
        );

        when(genreRepositoryMock.findAll()).thenReturn(genreList);

        List<Genre> response = genreService.getAllGenres();

        assertEquals(genreList, response, "ERROR: Lists was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreShouldReturnGenre() {
        Genre genre = new Genre("rock");

        when(genreRepositoryMock.findGenreByGenreIgnoreCase("rock")).thenReturn(genre);

        Genre response = genreService.findGenreByGenre("rock");

        assertEquals(genre, response, "ERROR: Objects was not identical");

        verify(genreRepositoryMock).findGenreByGenreIgnoreCase("rock");
    }

    @Test
    void findGenreByGenreTypeMusicShouldReturnMusicGenre() {
        Genre rock = new Genre("rock", "music");
        Genre comedy = new Genre("comedy", "video");
        List<Genre> genres = Arrays.asList(rock, comedy);

        when(genreRepositoryMock.findAll()).thenReturn(genres);

        Genre response = genreService.findGenreByGenreTypeMusic("rock");

        assertEquals(rock, response, "ERROR: Objects was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreTypeMusicShouldReturnException() {
        when(genreRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            genreService.findGenreByGenreTypeMusic("rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: NOT FOUND", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreTypePodShouldReturnGenre() {
        Genre business = new Genre("business", "pod");
        Genre documentary = new Genre("documentary", "video");
        List<Genre> genres = Arrays.asList(business, documentary);

        when(genreRepositoryMock.findAll()).thenReturn(genres);

        Genre response = genreService.findGenreByGenreTypePod("business");

        assertEquals(business, response, "ERROR: Objects was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreTypePodShouldReturnException() {
        when(genreRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            genreService.findGenreByGenreTypePod("comedy");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: NOT FOUND", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreTypeVideoShouldReturnGenre() {
        Genre comedy = new Genre("comedy", "video");
        Genre rock = new Genre("rock", "music");
        List<Genre> genres = Arrays.asList(rock, comedy);

        when(genreRepositoryMock.findAll()).thenReturn(genres);

        Genre response = genreService.findGenreByGenreTypeVideo("comedy");

        assertEquals(comedy, response, "ERROR: Objects was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void findGenreByGenreTypeVideoShouldReturnException() {
        when(genreRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            genreService.findGenreByGenreTypeVideo("action");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: NOT FOUND", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(genreRepositoryMock).findAll();
    }

    @Test
    void addPlayShouldReturnGenre() {
        Genre genre = new Genre("rock", "music");

        when(genreRepositoryMock.save(genre)).thenReturn(genre);

        Genre response = genreService.addPlay(genre);

        System.out.println(response.getTotalPlays());

        assertEquals(genre, response, "ERROR: Genre was not identical");

        verify(genreRepositoryMock).save(genre);
    }
}