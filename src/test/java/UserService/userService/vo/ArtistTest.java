package UserService.userService.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void constructorShouldReturnAnArtist() {
        Artist artist = new Artist();
        assertNotNull(artist);
    }

    @Test
    void constructorShouldReturnRightValueForName() {
        Artist artist = new Artist("Dire Straits");
        assertEquals("Dire Straits", artist.getName());
    }

    @Test
    void constructorShouldReturnRightValues() {
        Artist artist = new Artist(2, "Iron Maiden");
        assertEquals(2, artist.getId());
        assertEquals("Iron Maiden", artist.getName());
    }

    @Test
    void setIdShouldReplaceOldId() {
        Artist artist = new Artist(2, "Iron Maiden");
        artist.setId(5);
        assertEquals(5, artist.getId());
    }

    @Test
    void setNameShouldReplaceOldNameValue() {
        Artist artist = new Artist(2, "Iron Maiden");
        artist.setName("Lady Gaga");
        assertEquals("Lady Gaga", artist.getName());
    }
}