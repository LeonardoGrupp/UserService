package UserService.userService.services;

import UserService.userService.repositories.GenreRepository;
import UserService.userService.vo.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void findGenreByGenreTypeMusicShouldReturnGenre() {
    }

    @Test
    void findGenreByGenreTypePodShouldReturnGenre() {
    }

    @Test
    void findGenreByGenreTypeVideoShouldReturnGenre() {
    }

    @Test
    void addPlayShouldReturnGenre() {
    }
}