package UserService.userService.entites;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayedGenreTest {

    @Test
    void constructorShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre();
        assertNotNull(playedGenre);
    }

    @Test
    void constructorShouldReturnCorrectValues() {
        PlayedGenre playedGenre = new PlayedGenre("Opera", "video");
        assertEquals("Opera", playedGenre.getGenre());
        assertEquals("video", playedGenre.getType());
    }

    @Test
    void setIdShouldReplaceOldId() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setId(5);
        assertEquals(5, playedGenre.getId());
    }

    @Test
    void setGenreShouldReplaceOldGenre() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setGenre("Hip Hop");
        assertEquals("Hip Hop", playedGenre.getGenre());
    }

    @Test
    void setTypeShouldReplaceOldType() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setType("video");
        assertEquals("video", playedGenre.getType());
    }

    @Test
    void setTotalPlaysShouldReplaceOldTotalPlays() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setTotalPlays(13);
        assertEquals(13, playedGenre.getTotalPlays());
    }

    @Test
    void setLikedShouldReplaceOldValueOfLiked() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setLiked(true);
        assertTrue(playedGenre.isLiked());
    }

    @Test
    void setDislikedShouldReplaceOldValueOfDisliked() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.setDisliked(true);
        assertTrue(playedGenre.isDisliked());
    }

    @Test
    void countPlayShouldIncreaseValueOfTotalPlaysToOne() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.countPlay();
        assertEquals(1, playedGenre.getTotalPlays());
    }

    @Test
    void likeGenreShouldSetValueIsDislikedFalseIsLikedTrue() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.likeGenre();
        assertTrue(playedGenre.isLiked());
        assertFalse(playedGenre.isDisliked());
    }

    @Test
    void disLikeGenreShouldSetValueIsDislikedTrueIsLikedFalse() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.disLikeGenre();
        assertFalse(playedGenre.isLiked());
        assertTrue(playedGenre.isDisliked());
    }

    @Test
    void resetLikeAndDisLikeGenreShouldSetValueIsDislikedFalseIsLikedFalse() {
        PlayedGenre playedGenre = new PlayedGenre();
        playedGenre.resetLikeAndDisLikeGenre();
        assertFalse(playedGenre.isLiked());
        assertFalse(playedGenre.isDisliked());
    }
}