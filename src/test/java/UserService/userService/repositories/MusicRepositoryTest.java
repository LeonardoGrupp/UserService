package UserService.userService.repositories;

import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MusicRepositoryTest {

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private GenreRepository genreRepository;


    @Test
    void findByUrlIgnoreCaseShouldReturnMusic() {
        Music music = new Music("type", "title", "url", "release");
        musicRepository.save(music);

        Optional<Music> response = musicRepository.findByUrlIgnoreCase("url");

        assertEquals(music, response.get(), "ERROR: was not identical");
    }

    @Test
    void existsByUrlIgnoreCaseShouldReturnTrue() {
        Music music = new Music("Type", "Title", "url", "release");
        musicRepository.save(music);

        boolean isTrue = musicRepository.existsByUrlIgnoreCase("url");

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void existsByUrlIgnoreCaseShouldReturnFalse() {
        boolean isFalse = musicRepository.existsByUrlIgnoreCase("urlDoesNotExist");

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void findMusicByGenresShouldReturnList() {
        Genre jazz = new Genre("Jazz");
        genreRepository.save(jazz);
        List<Genre> genres = Arrays.asList(jazz);
        Music music = new Music("type", "title", "url", "release");
        music.setGenres(genres);

        musicRepository.save(music);

        List<Music> response = musicRepository.findMusicByGenres(jazz);

        assertEquals(1, response.size(), "ERROR: Sizes was not identical");
        assertEquals("Jazz", response.get(0).getGenres().get(0).getGenre(), "ERROR: Jazz was not the Genre");
        assertEquals(music, response.get(0), "ERROR: Music was not identical");
    }
}