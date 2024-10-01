package UserService.userService.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
        void constructorShouldReturnAGenre() {
        Genre genre = new Genre();
        assertNotNull(genre);
    }

    @Test
    void constructorShouldReturnRightValueForGenre() {
        Genre genre = new Genre("Jazz");
        assertEquals("Jazz", genre.getGenre());
    }

    @Test
    void constructorShouldReturnRightValueForGenreAndType() {
        Genre genre = new Genre("Jazz", "video");
        assertEquals("Jazz", genre.getGenre());
        assertEquals("video", genre.getType());
    }

    @Test
    void gettersAndSettersShouldReturnCorrectValues() {
        Genre genre = new Genre();
        genre.setId(4);
        genre.setGenre("Classical");
        genre.setType("music");
        genre.setTotalLikes(7);
        genre.setTotalPlays(10);
        assertEquals(4, genre.getId());
        assertEquals("Classical", genre.getGenre());
        assertEquals("music", genre.getType());
        assertEquals(7, genre.getTotalLikes());
        assertEquals(10, genre.getTotalPlays());
    }

    @Test
    void addPlayShouldIncreaseTotalPlaysWithOne() {
        Genre genre = new Genre();
        genre.addPlay();
        assertEquals(1, genre.getTotalPlays());
    }

    @Test
    void addLikeShouldIncreaseTotalLikesWithOne() {
        Genre genre = new Genre();
        genre.addLike();
        assertEquals(1, genre.getTotalLikes());
    }
}