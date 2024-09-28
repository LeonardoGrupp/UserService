package UserService.userService.repositories;

import UserService.userService.vo.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void findGenreByGenreIgnoreCaseShouldReturnGenre() {
        String genreName = "Rock";
        Genre genre = new Genre("Rock");
        genreRepository.save(genre);

        Genre response = genreRepository.findGenreByGenreIgnoreCase(genreName);

        assertEquals("Rock", response.getGenre(), "ERROR: Genre names was not identical");
    }
}