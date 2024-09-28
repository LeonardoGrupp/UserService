package UserService.userService.controllers;

import UserService.userService.repositories.GenreRepository;
import UserService.userService.services.GenreService;
import UserService.userService.vo.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreControllerTest {

    private GenreService genreServiceMock;
    private GenreController genreController;

    @BeforeEach
    void setUp() {
        genreServiceMock = mock(GenreService.class);
        genreController = new GenreController(genreServiceMock);
    }

    @Test
    void allGenresShouldReturnList() {
        List<Genre> allGenres = Arrays.asList(new Genre("Rock"), new Genre("Jazz"), new Genre("Pop"));

        when(genreServiceMock.getAllGenres()).thenReturn(allGenres);

        ResponseEntity<List<Genre>> response = genreController.allGenres();

        assertEquals(3, response.getBody().size(), "ERROR: Size was not identical");
        assertEquals("Rock", response.getBody().get(0).getGenre(), "ERROR: Genres was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(genreServiceMock).getAllGenres();
    }

    @Test
    void findGenreByNameShouldReturnGenre() {
        String genreName = "Rock";
        Genre genre = new Genre("Rock");

        when(genreServiceMock.findGenreByGenre(genreName)).thenReturn(genre);

        ResponseEntity<Genre> response = genreController.findGenreByName(genreName);

        assertEquals("Rock", response.getBody().getGenre(), "ERROR: Genres was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(genreServiceMock).findGenreByGenre(genreName);
    }
}