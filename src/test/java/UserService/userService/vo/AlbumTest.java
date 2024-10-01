package UserService.userService.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @Test
    void constructorShouldReturnAnAlbum() {
        Album album = new Album();
        assertNotNull(album);
    }

    @Test
    void constructorShouldReturnRightValueForName() {
        Album album = new Album("Dire Straits");
        assertEquals("Dire Straits", album.getName());
    }

    @Test
    void constructorShouldReturnRightValues() {
        Album album = new Album(2, "Iron Maiden");
        assertEquals(2, album.getId());
        assertEquals("Iron Maiden", album.getName());
    }

    @Test
    void setIdShouldReplaceOldId() {
        Album album = new Album(2, "Iron Maiden");
        album.setId(5);
        assertEquals(5, album.getId());
    }

    @Test
    void setNameShouldReplaceOldNameValue() {
        Album album = new Album(2, "Iron Maiden");
        album.setName("Lady Gaga");
        assertEquals("Lady Gaga", album.getName());
    }
}