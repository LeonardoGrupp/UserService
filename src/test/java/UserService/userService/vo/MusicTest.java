package UserService.userService.vo;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MusicTest {

    @Test
    void ConstructorShouldReturnMusic() {
        Music music = new Music();
        assertNotNull(music);
    }

    @Test
    void ConstructorShouldReturnRightValuesCombinedWithNullValues() {
        Music music = new Music("music","Music1", "URL1", "2024-12-24");
        assertEquals("music", music.getType());
        assertEquals("Music1", music.getTitle());
        assertEquals("URL1", music.getUrl());
        assertEquals("2024-12-24", music.getReleaseDate());
        assertNull(music.getGenres());
        assertNull(music.getAlbums());
        assertNull(music.getArtists());
    }

    @Test
    void ConstructorShouldReturnRightValues() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Heavy Metal"));
        genres.add(new Genre("Rock"));
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Rock in Rio"));
        albums.add(new Album("Alchemy"));
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Iron Maiden"));
        artists.add(new Artist("Dire Straits"));

        Music music = new Music(5, "Music1", "URL1", "2024-12-24", genres, albums, artists);

        assertEquals(5, music.getId());
        assertEquals("music", music.getType());
        assertEquals("Music1", music.getTitle());
        assertEquals("URL1", music.getUrl());
        assertEquals("2024-12-24", music.getReleaseDate());
        assertEquals("Rock", music.getGenres().get(1).getGenre());
        assertEquals("Rock in Rio", music.getAlbums().get(0).getName());
        assertEquals("Dire Straits", music.getArtists().get(1).getName());
    }

    @Test
    void setIdShouldSetFourAndGetIdShouldReturnFour() {
        Music music = new Music();
        music.setId(4);
        assertEquals(4, music.getId());
    }

    @Test
    void setTypeShouldSetVideoAndGetTypeShouldReturnVideo() {
        Music music = new Music();
        music.setType("video");
        assertEquals("video", music.getType());
    }

    @Test
    void setTitleShouldSetMusicTitleAndGetTitleShouldReturnMusicTitle() {
        Music music = new Music();
        music.setTitle("MusicTitle");
        assertEquals("MusicTitle", music.getTitle());
    }

    @Test
    void setUrlShouldSetNewUrlAndGetUrlShouldReturnNewUrl() {
        Music music = new Music();
        music.setUrl("NewUrl");
        assertEquals("NewUrl", music.getUrl());
    }

    @Test
    void setReleaseDateShouldSet2018AndGetUrlShouldReturn2018() {
        Music music = new Music();
        music.setReleaseDate("2018");
        assertEquals("2018", music.getReleaseDate());
    }

    @Test
    void setPlayCounterShouldSet13AndGetPlayCounterShouldReturn13() {
        Music music = new Music();
        music.setPlayCounter(13);
        assertEquals(13, music.getPlayCounter());
    }

    @Test
    void setLikesShouldSet11AndGetLikesShouldReturn11() {
        Music music = new Music();
        music.setLikes(11);
        assertEquals(11, music.getLikes());
    }

    @Test
    void setDisLikesShouldSet4AndGetDisLikesShouldReturn4() {
        Music music = new Music();
        music.setDisLikes(4);
        assertEquals(4, music.getDisLikes());
    }

    @Test
    void setGenreShouldUpdateGenreListAndGetGenreShouldReturnGenreList() {
        Music music = new Music();
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Rock"));
        genres.add(new Genre("Pop"));
        music.setGenres(genres);
        assertEquals("Pop", music.getGenres().get(1).getGenre());
    }

    @Test
    void setAlbumShouldUpdateAlbumListAndGetAlbumShouldReturnAlbumList() {
        Music music = new Music();
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Rock in Rio"));
        albums.add(new Album("Alchemy"));
        music.setAlbums(albums);
        assertEquals("Alchemy", music.getAlbums().get(1).getName());
    }

    @Test
    void setArtistShouldUpdateArtistListAndGetArtistShouldReturnArtistList() {
        Music music = new Music();
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Iron Maiden"));
        artists.add(new Artist("Dire Straits"));
        music.setArtists(artists);
        assertEquals("Dire Straits", music.getArtists().get(1).getName());
    }

    @Test
    void countPlayShouldIncreasePlayCounterFromZeroToOne() {
        Music music = new Music();
        music.countPlay();
        int result = music.getPlayCounter();
        assertEquals(1, result);
    }
}