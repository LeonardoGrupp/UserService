package UserService.userService.entites;

import UserService.userService.vo.Genre;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayedMediaTest {

    @Test
    void constructorShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia();
        assertNotNull(playedMedia);
    }

    @Test
    void constructorShouldReturnCorrectValues() {
        PlayedMedia playedMedia = new PlayedMedia("video", "Super Duper Video", "Url1", "2020-01-01");
        assertEquals("video", playedMedia.getType());
        assertEquals("Super Duper Video", playedMedia.getTitle());
        assertEquals("Url1", playedMedia.getUrl());
        assertEquals("2020-01-01", playedMedia.getReleaseDate());
        assertNotNull(playedMedia.getGenres());
    }

    @Test
    void constructorShouldReturnCorrectValueForListOfGenres() {
        List<PlayedGenre> genres = new ArrayList<>();
        genres.add(new PlayedGenre("Rock", "video"));
        genres.add(new PlayedGenre("Opera", "music"));
        PlayedMedia playedMedia = new PlayedMedia("video", "Super Duper Video", "Url1", "2020-01-01", genres);
        assertEquals("Opera", playedMedia.getGenres().get(1).getGenre());
    }

    @Test
    void constructorShouldReturnCorrectValues2() {
        PlayedMedia playedMedia = new PlayedMedia(2, "pod", "Super Duper Video", "Url1", "2020-01-01", 13, true, false);
        assertEquals("pod", playedMedia.getType());
        assertEquals("Super Duper Video", playedMedia.getTitle());
        assertEquals("Url1", playedMedia.getUrl());
        assertEquals("2020-01-01", playedMedia.getReleaseDate());
        assertEquals(13, playedMedia.getTimesPlayed());
        assertTrue(playedMedia.isLiked());
        assertFalse(playedMedia.isDisliked());
    }

    @Test
    void setIdShouldReplaceOldId() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setId(5);
        assertEquals(5, playedMedia.getId());
    }

    @Test
    void setTypeShouldReplaceOldType() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setType("video");
        assertEquals("video", playedMedia.getType());
    }

    @Test
    void setTitleShouldReplaceOldTitle() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setTitle("Hip Hop");
        assertEquals("Hip Hop", playedMedia.getTitle());
    }

    @Test
    void setUrlShouldReplaceOldUrl() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setUrl("Url13");
        assertEquals("Url13", playedMedia.getUrl());
    }

    @Test
    void setReleaseDateShouldReplaceReleaseDate() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setReleaseDate("1992-12-12");
        assertEquals("1992-12-12", playedMedia.getReleaseDate());
    }

    @Test
    void setTimesPlayedShouldReplaceTimesPlayed() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setTimesPlayed(123);
        assertEquals(123, playedMedia.getTimesPlayed());
    }

    @Test
    void setLikedShouldReplaceOldValueOfLiked() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setLiked(true);
        assertTrue(playedMedia.isLiked());
    }

    @Test
    void setDislikedShouldReplaceOldValueOfDisliked() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setDisliked(true);
        assertTrue(playedMedia.isDisliked());
    }

    @Test
    void setGenresShouldReplaceOldGenre() {
        List<PlayedGenre> genres = new ArrayList<>();
        genres.add(new PlayedGenre("Rock", "video"));
        genres.add(new PlayedGenre("Opera", "music"));
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.setGenres(genres);
        assertEquals("Opera", playedMedia.getGenres().get(1).getGenre());
    }

    @Test
    void countPlayShouldIncreaseValueOfTimesPlayedToOne() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.countPlay();
        assertEquals(1, playedMedia.getTimesPlayed());
    }

    @Test
    void addPlayedGenreToMediaShouldReturnRockFromIndexZero() {
        PlayedMedia playedMedia = new PlayedMedia();
        PlayedGenre genre = new PlayedGenre("Rock", "video");
        playedMedia.addPlayedGenreToMedia(genre);
        assertEquals("Rock", playedMedia.getGenres().get(0).getGenre());
    }

    @Test
    void likeMediaShouldSetValueIsDislikedFalseIsLikedTrue() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.likeMedia();
        assertTrue(playedMedia.isLiked());
        assertFalse(playedMedia.isDisliked());
    }

    @Test
    void disLikeMediaShouldSetValueIsDislikedFalseIsDisLikedTrue() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.disLikeMedia();
        assertFalse(playedMedia.isLiked());
        assertTrue(playedMedia.isDisliked());
    }

    @Test
    void resetLikeAndDisLikeMediaShouldSetValueIsDislikedFalseIsLikedFalse() {
        PlayedMedia playedMedia = new PlayedMedia();
        playedMedia.resetLikeAndDisLikeMedia();
        assertFalse(playedMedia.isLiked());
        assertFalse(playedMedia.isDisliked());
    }
}