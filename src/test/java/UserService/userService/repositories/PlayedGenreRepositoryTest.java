package UserService.userService.repositories;

import UserService.userService.entites.PlayedGenre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlayedGenreRepositoryTest {

    @Autowired
    private PlayedGenreRepository playedGenreRepository;

    @Test
    void findPlayedGenreByGenreIgnoreCase() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        playedGenreRepository.save(playedGenre);

        PlayedGenre response = playedGenreRepository.findPlayedGenreByGenreIgnoreCase("rock");

        assertEquals("rock", response.getGenre(), "ERROR: genre names was not identical");
        assertEquals(playedGenre, response, "ERROR: was not identical");
    }
}