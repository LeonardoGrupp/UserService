package UserService.userService.entites;

import UserService.userService.vo.Video;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorShouldReturnUser() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void constructorShouldReturnUsername() {
        User user = new User("Försökskanin");
        assertEquals("Försökskanin", user.getUsername());
    }

    @Test
    void constructorShouldReturnIdAndUsername() {
        User user = new User(12,"Bapelsin");
        assertEquals(12,user.getId());
        assertEquals("Bapelsin", user.getUsername());
    }

    @Test
    void setIdShouldReplaceOldId() {
        User user = new User();
        user.setId(5);
        assertEquals(5, user.getId());
    }

    @Test
    void setUsernameShouldReplaceOldUsername() {
        User user = new User("Cow Milker");
        user.setUsername("Cheese Man");
        assertEquals("Cheese Man", user.getUsername());
    }

    @Test
    void setLikedMediaShouldReplaceOldLikedMediaList() {
        User user = new User("Cheese Man");
        List<PlayedMedia> likedMediaList = new ArrayList<>();
        likedMediaList.add(new PlayedMedia("video", "Mer Jul", "url1","1989-12-24"));
        likedMediaList.add(new PlayedMedia("video", "Sommartider", "url3","1980-07-05"));
        user.setLikedMedia(likedMediaList);
        assertEquals("Mer Jul", user.getLikedMedia().get(0).getTitle());
        assertEquals("Sommartider", user.getLikedMedia().get(1).getTitle());
    }

    @Test
    void setDisLikedMediaShouldReplaceOldDisLikedMediaList() {
        User user = new User("Cow Milker");
        List<PlayedMedia> DislikedMediaList = new ArrayList<>();
        DislikedMediaList.add(new PlayedMedia("video", "Mer Jul", "url1","1989-12-24"));
        DislikedMediaList.add(new PlayedMedia("video", "Sommartider", "url3","1980-07-05"));
        user.setDisLikedMedia(DislikedMediaList);
        assertEquals("Mer Jul", user.getDisLikedMedia().get(0).getTitle());
        assertEquals("Sommartider", user.getDisLikedMedia().get(1).getTitle());
    }

    @Test
    void setPlayedMediaShouldReplaceOldPlayedMediaList() {
        User user = new User("Cow Milker");
        List<PlayedMedia> playedMediaList = new ArrayList<>();
        playedMediaList.add(new PlayedMedia("video", "Mer Jul", "url1","1989-12-24"));
        playedMediaList.add(new PlayedMedia("video", "Sommartider", "url3","1980-07-05"));
        user.setPlayedMedia(playedMediaList);
        assertEquals("Mer Jul", user.getPlayedMedia().get(0).getTitle());
        assertEquals("Sommartider", user.getPlayedMedia().get(1).getTitle());
    }

    @Test
    void setPlayedGenreShouldReplaceOldPlayedGenreList() {
        User user = new User("Cow Milker");
        List<PlayedGenre> playedGenreList = new ArrayList<>();
        playedGenreList.add(new PlayedGenre("Opera", "music"));
        playedGenreList.add(new PlayedGenre("Hip Hop", "pod"));
        user.setPlayedGenre(playedGenreList);
        assertEquals("Opera", user.getPlayedGenre().get(0).getGenre());
        assertEquals("pod", user.getPlayedGenre().get(1).getType());
    }

    @Test
    void setLikedGenreShouldReplaceOldLikedGenreList() {
        User user = new User("Cow Milker");
        List<PlayedGenre> likedGenreList = new ArrayList<>();
        likedGenreList.add(new PlayedGenre("Opera", "music"));
        likedGenreList.add(new PlayedGenre("Hip Hop", "pod"));
        user.setLikedGenre(likedGenreList);
        assertEquals("Opera", user.getLikedGenre().get(0).getGenre());
        assertEquals("pod", user.getLikedGenre().get(1).getType());
    }

    @Test
    void setDisLikedGenreShouldReplaceOldDisLikedGenreList() {
        User user = new User("Cow Milker");
        List<PlayedGenre> dislikedGenreList = new ArrayList<>();
        dislikedGenreList.add(new PlayedGenre("Opera", "music"));
        dislikedGenreList.add(new PlayedGenre("Hip Hop", "pod"));
        user.setDisLikedGenre(dislikedGenreList);
        assertEquals("Opera", user.getDisLikedGenre().get(0).getGenre());
        assertEquals("pod", user.getDisLikedGenre().get(1).getType());
    }

    @Test
    void addMediaToPlayedMediaShouldReturnAddedMedia() {
        User user = new User("Cow Milker");
        PlayedMedia playedMedia = new PlayedMedia("video", "Mer Jul", "url3","1980-07-05");
        user.addMediaToPlayedMedia(playedMedia);
        assertEquals(playedMedia, user.getPlayedMedia().get(0));
    }

    @Test
    void addGenreToPlayedGenreShouldReturnAddedGenre() {
        User user = new User("Cow Milker");
        PlayedGenre playedGenre = new PlayedGenre("Rock'n Roll", "music");
        user.addGenreToPlayedGenre(playedGenre);
        assertEquals(playedGenre, user.getPlayedGenre().get(0));
    }

    @Test
    void removeOrAddMediaFromDislikedAndLikedMediaShouldRemoveMediaFromDislikedMedia() {
        User user = new User("Cow Milker");
        List<PlayedMedia> dislikedMediaList = new ArrayList<>();
        PlayedMedia playedMedia1 = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        PlayedMedia playedMedia2 = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        dislikedMediaList.add(playedMedia1);
        dislikedMediaList.add(playedMedia2);
        user.setDisLikedMedia(dislikedMediaList);
        assertEquals(playedMedia1, user.getDisLikedMedia().get(0));
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia1);
        assertNotEquals(playedMedia1, user.getDisLikedMedia().get(0));
    }

    @Test
    void removeOrAddMediaFromDislikedAndLikedMediaShouldAddMediaToDislikedMediaWhenNotExists() {
        User user = new User("Cow Milker");
        PlayedMedia playedMedia = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        playedMedia.disLikeMedia();
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
        assertEquals(playedMedia, user.getDisLikedMedia().get(0));
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
        assertEquals(1, user.getDisLikedMedia().size());
    }

    @Test
    void removeOrAddMediaFromDislikedAndLikedMediaShouldRemoveMediaFromLikedMedia() {
        User user = new User("Cow Milker");
        List<PlayedMedia> likedMediaList = new ArrayList<>();
        PlayedMedia playedMedia1 = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        PlayedMedia playedMedia2 = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        likedMediaList.add(playedMedia1);
        likedMediaList.add(playedMedia2);
        user.setLikedMedia(likedMediaList);
        assertEquals(playedMedia1, user.getLikedMedia().get(0));
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia1);
        assertNotEquals(playedMedia1, user.getLikedMedia().get(0));
    }

    @Test
    void removeOrAddMediaFromDislikedAndLikedMediaShouldAddMediaToLikedMediaWhenNotExists() {
        User user = new User("Cow Milker");
        PlayedMedia playedMedia = new PlayedMedia("video", "Mer Jul", "url1","1989-12-24");
        playedMedia.likeMedia();
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
        assertEquals(playedMedia, user.getLikedMedia().get(0));
        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
        assertEquals(1, user.getLikedMedia().size());
    }

    @Test
    void removeOrAddGenreFromDislikedAndLikedGenreShouldRemoveGenreFromDislikedGenre() {
        User user = new User("Cow Milker");
        List<PlayedGenre> dislikedGenreList = new ArrayList<>();
        PlayedGenre playedGenre1 = new PlayedGenre("Pop", "video");
        PlayedGenre playedGenre2 = new PlayedGenre("Jazz", "music");
        dislikedGenreList.add(playedGenre1);
        dislikedGenreList.add(playedGenre2);
        user.setDisLikedGenre(dislikedGenreList);
        assertEquals(playedGenre1, user.getDisLikedGenre().get(0));
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre1);
        assertNotEquals(playedGenre1, user.getDisLikedGenre().get(0));
    }

    @Test
    void removeOrAddGenreFromDislikedAndLikedGenreShouldAddGenreToDislikedGenreWhenNotExists() {
        User user = new User("Cow Milker");
        PlayedGenre playedGenre = new PlayedGenre("Pop", "video");
        playedGenre.disLikeGenre();
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
        assertEquals(playedGenre, user.getDisLikedGenre().get(0));
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
        assertEquals(1, user.getDisLikedGenre().size());
    }

    @Test
    void removeOrAddGenreFromDislikedAndLikedGenreShouldRemoveGenreFromLikedGenre() {
        User user = new User("Cow Milker");
        List<PlayedGenre> likedGenreList = new ArrayList<>();
        PlayedGenre playedGenre1 = new PlayedGenre("Pop", "video");
        PlayedGenre playedGenre2 = new PlayedGenre("Jazz", "music");
        likedGenreList.add(playedGenre1);
        likedGenreList.add(playedGenre2);
        user.setLikedGenre(likedGenreList);
        assertEquals(playedGenre1, user.getLikedGenre().get(0));
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre1);
        assertNotEquals(playedGenre1, user.getLikedGenre().get(0));
    }

    @Test
    void removeOrAddGenreFromDislikedAndLikedGenreShouldAddGenreToLikedGenreWhenNotExists() {
        User user = new User("Cow Milker");
        PlayedGenre playedGenre = new PlayedGenre("Pop", "video");
        playedGenre.likeGenre();
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
        assertEquals(playedGenre, user.getLikedGenre().get(0));
        user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
        assertEquals(1, user.getLikedGenre().size());
    }
}