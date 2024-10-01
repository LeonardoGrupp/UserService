package UserService.userService.vo;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VideoTest {

    @Test
    void ConstructorShouldReturnVideo() {
        Video video = new Video();
        assertNotNull(video);
    }

    @Test
    void ConstructorShouldReturnRightValuesCombinedWithNullValues() {
        Video video = new Video("video","Video1", "URL1", "2024-12-24");
        assertEquals("video", video.getType());
        assertEquals("Video1", video.getTitle());
        assertEquals("URL1", video.getUrl());
        assertEquals("2024-12-24", video.getReleaseDate());
        assertNull(video.getGenres());
        assertNull(video.getAlbums());
        assertNull(video.getArtists());
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

        Video video = new Video(5, "video", "Video1", "URL1", "2024-12-24", genres, albums, artists);

        assertEquals(5, video.getId());
        assertEquals("video", video.getType());
        assertEquals("Video1", video.getTitle());
        assertEquals("URL1", video.getUrl());
        assertEquals("2024-12-24", video.getReleaseDate());
        assertEquals("Rock", video.getGenres().get(1).getGenre());
        assertEquals("Rock in Rio", video.getAlbums().get(0).getName());
        assertEquals("Dire Straits", video.getArtists().get(1).getName());
    }

    @Test
    void setIdShouldSetFourAndGetIdShouldReturnFour() {
        Video video = new Video();
        video.setId(4);
        assertEquals(4, video.getId());
    }

    @Test
    void setTypeShouldSetVideoAndGetTypeShouldReturnVideo() {
        Video video = new Video();
        video.setType("video");
        assertEquals("video", video.getType());
    }

    @Test
    void setTitleShouldSetVideoTitleAndGetTitleShouldReturnVideoTitle() {
        Video video = new Video();
        video.setTitle("VideoTitle");
        assertEquals("VideoTitle", video.getTitle());
    }

    @Test
    void setUrlShouldSetNewUrlAndGetUrlShouldReturnNewUrl() {
        Video video = new Video();
        video.setUrl("NewUrl");
        assertEquals("NewUrl", video.getUrl());
    }

    @Test
    void setReleaseDateShouldSet2018AndGetUrlShouldReturn2018() {
        Video video = new Video();
        video.setReleaseDate("2018");
        assertEquals("2018", video.getReleaseDate());
    }

    @Test
    void setPlayCounterShouldSet13AndGetPlayCounterShouldReturn13() {
        Video video = new Video();
        video.setPlayCounter(13);
        assertEquals(13, video.getPlayCounter());
    }

    @Test
    void setLikesShouldSet11AndGetLikesShouldReturn11() {
        Video video = new Video();
        video.setLikes(11);
        assertEquals(11, video.getLikes());
    }

    @Test
    void setDisLikesShouldSet4AndGetDisLikesShouldReturn4() {
        Video video = new Video();
        video.setDisLikes(4);
        assertEquals(4, video.getDisLikes());
    }

    @Test
    void setGenreShouldUpdateGenreListAndGetGenreShouldReturnGenreList() {
        Video video = new Video();
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Rock"));
        genres.add(new Genre("Pop"));
        video.setGenres(genres);
        assertEquals("Pop", video.getGenres().get(1).getGenre());
    }

    @Test
    void setAlbumShouldUpdateAlbumListAndGetAlbumShouldReturnAlbumList() {
        Video video = new Video();
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Rock in Rio"));
        albums.add(new Album("Alchemy"));
        video.setAlbums(albums);
        assertEquals("Alchemy", video.getAlbums().get(1).getName());
    }

    @Test
    void setArtistShouldUpdateArtistListAndGetArtistShouldReturnArtistList() {
        Video video = new Video();
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Iron Maiden"));
        artists.add(new Artist("Dire Straits"));
        video.setArtists(artists);
        assertEquals("Dire Straits", video.getArtists().get(1).getName());
    }

    @Test
    void countPlayShouldIncreasePlayCounterFromZeroToOne() {
        Video video = new Video();
        video.countPlay();
        int result = video.getPlayCounter();
        assertEquals(1, result);
    }
}