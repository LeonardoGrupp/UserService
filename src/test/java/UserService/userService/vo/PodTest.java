package UserService.userService.vo;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PodTest {

    @Test
    void ConstructorShouldReturnPod() {
        Pod pod = new Pod();
        assertNotNull(pod);
    }

    @Test
    void ConstructorShouldReturnRightValuesCombinedWithNullValues() {
        Pod pod = new Pod("pod","Pod1", "URL1", "2024-12-24");
        assertEquals("pod", pod.getType());
        assertEquals("Pod1", pod.getTitle());
        assertEquals("URL1", pod.getUrl());
        assertEquals("2024-12-24", pod.getReleaseDate());
        assertNull(pod.getGenres());
        assertNull(pod.getAlbums());
        assertNull(pod.getArtists());
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

        Pod pod = new Pod(5, "pod", "Pod1", "URL1", "2024-12-24", genres, albums, artists);

        assertEquals(5, pod.getId());
        assertEquals("pod", pod.getType());
        assertEquals("Pod1", pod.getTitle());
        assertEquals("URL1", pod.getUrl());
        assertEquals("2024-12-24", pod.getReleaseDate());
        assertEquals("Rock", pod.getGenres().get(1).getGenre());
        assertEquals("Rock in Rio", pod.getAlbums().get(0).getName());
        assertEquals("Dire Straits", pod.getArtists().get(1).getName());
    }

    @Test
    void setIdShouldSetFourAndGetIdShouldReturnFour() {
        Pod pod = new Pod();
        pod.setId(4);
        assertEquals(4, pod.getId());
    }

    @Test
    void setTypeShouldSetVideoAndGetTypeShouldReturnVideo() {
        Pod pod = new Pod();
        pod.setType("video");
        assertEquals("video", pod.getType());
    }

    @Test
    void setTitleShouldSetPodTitleAndGetTitleShouldReturnPodTitle() {
        Pod pod = new Pod();
        pod.setTitle("PodTitle");
        assertEquals("PodTitle", pod.getTitle());
    }

    @Test
    void setUrlShouldSetNewUrlAndGetUrlShouldReturnNewUrl() {
        Pod pod = new Pod();
        pod.setUrl("NewUrl");
        assertEquals("NewUrl", pod.getUrl());
    }

    @Test
    void setReleaseDateShouldSet2018AndGetUrlShouldReturn2018() {
        Pod pod = new Pod();
        pod.setReleaseDate("2018");
        assertEquals("2018", pod.getReleaseDate());
    }

    @Test
    void setPlayCounterShouldSet13AndGetPlayCounterShouldReturn13() {
        Pod pod = new Pod();
        pod.setPlayCounter(13);
        assertEquals(13, pod.getPlayCounter());
    }

    @Test
    void setLikesShouldSet11AndGetLikesShouldReturn11() {
        Pod pod = new Pod();
        pod.setLikes(11);
        assertEquals(11, pod.getLikes());
    }

    @Test
    void setDisLikesShouldSet4AndGetDisLikesShouldReturn4() {
        Pod pod = new Pod();
        pod.setDisLikes(4);
        assertEquals(4, pod.getDisLikes());
    }

    @Test
    void setGenreShouldUpdateGenreListAndGetGenreShouldReturnGenreList() {
        Pod pod = new Pod();
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Rock"));
        genres.add(new Genre("Pop"));
        pod.setGenres(genres);
        assertEquals("Pop", pod.getGenres().get(1).getGenre());
    }

    @Test
    void setAlbumShouldUpdateAlbumListAndGetAlbumShouldReturnAlbumList() {
        Pod pod = new Pod();
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Rock in Rio"));
        albums.add(new Album("Alchemy"));
        pod.setAlbums(albums);
        assertEquals("Alchemy", pod.getAlbums().get(1).getName());
    }

    @Test
    void setArtistShouldUpdateArtistListAndGetArtistShouldReturnArtistList() {
        Pod pod = new Pod();
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist("Iron Maiden"));
        artists.add(new Artist("Dire Straits"));
        pod.setArtists(artists);
        assertEquals("Dire Straits", pod.getArtists().get(1).getName());
    }

    @Test
    void countPlayShouldIncreasePlayCounterFromZeroToOne() {
        Pod pod = new Pod();
        pod.countPlay();
        int result = pod.getPlayCounter();
        assertEquals(1, result);
    }
}