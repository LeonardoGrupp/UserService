package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.repositories.UserRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import UserService.userService.vo.Pod;
import UserService.userService.vo.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepositoryMock;
    private PlayedMediaService playedMediaServiceMock;
    private PlayedGenreService playedGenreServiceMock;
    private MusicService musicServiceMock;
    private PodService podServiceMock;
    private VideoService videoServiceMock;
    private GenreService genreServiceMock;

    @BeforeEach
    void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        playedMediaServiceMock = mock(PlayedMediaService.class);
        playedGenreServiceMock = mock(PlayedGenreService.class);
        musicServiceMock = mock(MusicService.class);
        podServiceMock = mock(PodService.class);
        videoServiceMock = mock(VideoService.class);
        genreServiceMock = mock(GenreService.class);
        userService = new UserService(userRepositoryMock, playedMediaServiceMock, playedGenreServiceMock, musicServiceMock, videoServiceMock, podServiceMock, genreServiceMock);

    }

    @Test
    void findAllUsersShouldReturnList() {
        List<User> userList = Arrays.asList(
                new User("freddan"),
                new User("jacob"),
                new User("petter"),
                new User("fahri")
        );

        when(userRepositoryMock.findAll()).thenReturn(userList);

        List<User> response = userService.findAllUsers();

        assertEquals(userList, response, "ERROR: Lists was not identical");

        verify(userRepositoryMock).findAll();
    }

    @Test
    void findUserByIdShouldReturnUser() {
        User user = new User("freddan");
        long id = 1;
        user.setId(id);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        User response = userService.findUserById(id);

        assertEquals(user, response, "ERROR: Objects was not identical");

        verify(userRepositoryMock).findById(id);
    }

    @Test
    void findUserByIdShouldReturnException() {
        long id = 1;

        when(userRepositoryMock.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.findUserById(id);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userRepositoryMock).findById(id);
    }

    @Test
    void findUserByUsernameShouldReturnUser() {
        User user = new User("freddan");

        when(userRepositoryMock.findUserByUsername("freddan")).thenReturn(user);

        User response = userService.findUserByUsername("freddan");

        assertEquals("freddan", response.getUsername(), "ERROR: Usernames was not identical");

        verify(userRepositoryMock).findUserByUsername("freddan");
    }

    @Test
    void findUserByUsernameShouldReturnException() {
        when(userRepositoryMock.findUserByUsername("freddan")).thenReturn(null);

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.findUserByUsername("freddan");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userRepositoryMock).findUserByUsername("freddan");
    }

    @Test
    void createShouldReturnUser() {
        User user = new User("freddan");

        when(userRepositoryMock.save(user)).thenReturn(user);

        User response = userService.create(user);

        assertEquals(user, response, "ERROR: Objects was not identical");

        verify(userRepositoryMock, atLeastOnce()).save(user);
    }

    @Test
    void createShouldReturnException() {
        User user = new User("");

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.create(user);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: Username not provided", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void updateUserShouldReturnUser() {
        long userId = 1;
        User user = new User("freddan");
        user.setId(userId);

        User newInfo = new User("update");

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(user)).thenReturn(newInfo);

        User response = userService.updateUser(userId, newInfo);

        assertEquals("update", response.getUsername(), "ERROR: Usernames was not identical");

        verify(userRepositoryMock).findById(userId);
        verify(userRepositoryMock).save(user);
    }

    @Test
    void updateUserWhenUserDontExistShouldReturnException() {
        long userId = 1;
        User newInfo = new User("new");

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.updateUser(userId, newInfo);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userRepositoryMock).findById(userId);
    }

    @Test
    void deleteShouldReturnString() {
        String expected = "User successfully deleted";

        long userId = 1;
        User user = new User("freddan");
        user.setId(userId);

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));

        String response = userService.delete(userId);

        assertEquals(expected, response, "ERROR: Strings was not identical");

        verify(userRepositoryMock).findById(userId);
    }

    @Test
    void playMedia() {
    }

    @Test
    void likeMediaShouldReturnPlayedMedia() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");
        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        PlayedMedia response = userService.likeMedia(id, url);

        assertTrue(response.isLiked(), "ERROR: was False");
    }

    @Test
    void likeMediaWithoutPlayingItFirstShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.likeMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: In order to like media you first need to play it", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void likeMediaThatIsAlreadyLikedShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");

        playedMedia.likeMedia();

        List<PlayedMedia> likedList = Arrays.asList(playedMedia);
        user.setLikedMedia(likedList);

        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.likeMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: media already liked", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void likeGenreShouldReturnPlayedGenre() {
        User user = new User("freddan");
        long id = 1;

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(playedGenreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(playedGenreServiceMock.save(rock)).thenReturn(rock);

        PlayedGenre response = userService.likeGenre(id, "rock");

        assertTrue(response.isLiked(), "ERROR: was not Liked");
    }

    @Test
    void likeGenreWhenGenreIsAlreadyLikedShouldReturnException() {
        User user = new User("freddan");
        long id = 1;

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");

        rock.likeGenre();

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(playedGenreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.likeGenre(id, "rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: Already liked genre", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void likeGenreWhenGenreNotFoundShouldReturnException() {
        User user = new User("freddan");
        long id = 1;

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.likeGenre(id, "rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void disLikeMediaShouldReturnPlayedMedia() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");
        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        PlayedMedia response = userService.disLikeMedia(id, url);

        assertTrue(response.isDisliked(), "ERROR: was False");
    }

    @Test
    void dislikeMediaWithoutPlayingItFirstShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.disLikeMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: In order to dislike media you first need to play it", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void dislikeMediaThatIsAlreadyLikedShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");

        playedMedia.disLikeMedia();

        List<PlayedMedia> dislikedList = Arrays.asList(playedMedia);
        user.setDisLikedMedia(dislikedList);

        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.disLikeMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: media already disliked", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void dislikeGenreShouldReturnPlayedGenre() {
        User user = new User("freddan");
        long id = 1;

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(playedGenreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(playedGenreServiceMock.save(rock)).thenReturn(rock);

        PlayedGenre response = userService.disLikeGenre(id, "rock");

        assertTrue(response.isDisliked(), "ERROR: was not disliked");
    }

    @Test
    void dislikeGenreWhenGenreIsAlreadyLikedShouldReturnException() {
        User user = new User("freddan");
        long id = 1;

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");

        rock.disLikeGenre();

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(playedGenreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.disLikeGenre(id, "rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: Already disliked genre", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void dislikeGenreWhenGenreNotFoundShouldReturnException() {
        User user = new User("freddan");
        long id = 1;

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.disLikeGenre(id, "rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void resetLikesAndDisLikesOfMediaShouldReturnPlayedMedia() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");

        playedMedia.disLikeMedia();

        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        PlayedMedia response = userService.resetLikesAndDisLikesOfMedia(id, url);

        assertFalse(response.isDisliked(), "ERROR: was True");
        assertFalse(response.isLiked(), "ERROR: was True");
    }

    @Test
    void resetLikesAndDisLikesOfMediaWithoutPlayingItFirstShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);
        userRepositoryMock.save(user);

        String url = "url";

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.resetLikesAndDisLikesOfMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: In order to reset likes/dislikes media you first need to play it", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void resetLikesAndDisLikesOfMediaThatIsAlreadyFalseShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        PlayedMedia playedMedia2 = new PlayedMedia("music", "title", "url2", "release");

        List<PlayedMedia> mediaList = Arrays.asList(playedMedia, playedMedia2);
        user.setPlayedMedia(mediaList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.resetLikesAndDisLikesOfMedia(id, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: Media is already false on both like and dislike", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void resetLikesAndDisLikesOfGenreShouldReturnPlayedGenre() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);

        String type = "music";

        PlayedGenre rock = new PlayedGenre("rock", type);
        PlayedGenre jazz = new PlayedGenre("jazz", type);

        rock.disLikeGenre();

        List<PlayedGenre> genreList = Arrays.asList(rock, jazz);
        user.setPlayedGenre(genreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        PlayedGenre response = userService.resetLikesAndDisLikesOfGenre(id, "rock");

        assertFalse(response.isDisliked(), "ERROR: was True");
        assertFalse(response.isLiked(), "ERROR: was True");
    }

    @Test
    void resetLikesAndDisLikesOfGenreWhenFalseShouldReturnException() {
        long id = 1;
        User user = new User("freddan");
        user.setId(id);

        String type = "music";

        PlayedGenre rock = new PlayedGenre("rock", type);
        PlayedGenre jazz = new PlayedGenre("jazz", type);

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);
        user.setPlayedGenre(playedGenreList);

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.resetLikesAndDisLikesOfGenre(id, "rock");
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: Genre is already false on both like and dislike", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void findMusicByUrlShouldReturnMusic() {
        Music music = new Music("music", "title", "url", "release");

        when(musicServiceMock.findMusicByUrl("url")).thenReturn(music);

        Music response = userService.findMusicByUrl("url");

        assertEquals("title", response.getTitle(), "ERROR: Titles was not identical");

        verify(musicServiceMock).findMusicByUrl("url");
    }

    @Test
    void isMusicShouldReturnTrue() {
        when(musicServiceMock.musicExistsByUrl("url")).thenReturn(true);

        boolean isTrue = userService.isMusic("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(musicServiceMock).musicExistsByUrl("url");
    }

    @Test
    void isMusicShouldReturnFalse() {
        when(musicServiceMock.musicExistsByUrl("url")).thenReturn(false);

        boolean isFalse = userService.isMusic("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(musicServiceMock).musicExistsByUrl("url");
    }

    @Test
    void isPodShouldReturnTrue() {
        when(podServiceMock.podExistsByUrl("url")).thenReturn(true);

        boolean isTrue = userService.isPod("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(podServiceMock).podExistsByUrl("url");
    }

    @Test
    void isPodShouldReturnFalse() {
        when(podServiceMock.podExistsByUrl("url")).thenReturn(false);

        boolean isFalse = userService.isPod("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(podServiceMock).podExistsByUrl("url");
    }

    @Test
    void isVideoShouldReturnTrue() {
        when(videoServiceMock.videoExistsByUrl("url")).thenReturn(true);

        boolean isTrue = userService.isVideo("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(videoServiceMock).videoExistsByUrl("url");
    }

    @Test
    void isVideoShouldReturnFalse() {
        when(videoServiceMock.videoExistsByUrl("url")).thenReturn(false);

        boolean isFalse = userService.isVideo("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(videoServiceMock).videoExistsByUrl("url");
    }

    @Test
    void hasPlayedMediaBeforeShouldReturnTrue() {
        User user = new User("freddan");

        String url = "url";

        PlayedMedia playedMedia = new PlayedMedia("music", "title", url, "release");
        List<PlayedMedia> playedMediaList = Arrays.asList(playedMedia);
        user.setPlayedMedia(playedMediaList);

        boolean isTrue = userService.hasPlayedMediaBefore(user, url);

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void hasPlayedMediaBeforeShouldReturnFalse() {
        User user = new User("freddan");

        String url = "doest not exist";

        boolean isFalse = userService.hasPlayedMediaBefore(user, url);

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void hasPlayedMusicGenreBeforeShouldReturnTrue() {
        User user = new User("freddan");

        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        List<PlayedGenre> playedGenreList = Arrays.asList(playedGenre);
        user.setPlayedGenre(playedGenreList);

        Genre genre = new Genre("rock", "music");

        boolean isTrue = userService.hasPlayedMusicGenreBefore(user, genre);

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void hasPlayedMusicGenreBeforeShouldReturnFalse() {
        User user = new User("freddan");

        Genre genre = new Genre("rock", "music");

        boolean isFalse = userService.hasPlayedMusicGenreBefore(user, genre);

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void hasPlayedPodGenreBeforeShouldReturnTrue() {
        User user = new User("freddan");

        PlayedGenre playedGenre = new PlayedGenre("business", "pod");
        List<PlayedGenre> playedGenreList = Arrays.asList(playedGenre);
        user.setPlayedGenre(playedGenreList);

        Genre genre = new Genre("business", "pod");

        boolean isTrue = userService.hasPlayedPodGenreBefore(user, genre);

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void hasPlayedPodGenreBeforeShouldReturnFalse() {
        User user = new User("freddan");

        Genre genre = new Genre("business", "pod");

        boolean isFalse = userService.hasPlayedPodGenreBefore(user, genre);

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void hasPlayedVideoGenreBeforeShouldReturnTrue() {
        User user = new User("freddan");

        PlayedGenre playedGenre = new PlayedGenre("action", "video");
        List<PlayedGenre> playedGenreList = Arrays.asList(playedGenre);
        user.setPlayedGenre(playedGenreList);

        Genre genre = new Genre("action", "video");

        boolean isTrue = userService.hasPlayedVideoGenreBefore(user, genre);

        assertTrue(isTrue, "ERROR: was False");
    }

    @Test
    void hasPlayedVideoGenreBeforeShouldReturnFalse() {
        User user = new User("freddan");

        Genre genre = new Genre("action", "video");

        boolean isFalse = userService.hasPlayedVideoGenreBefore(user, genre);

        assertFalse(isFalse, "ERROR: was True");
    }

    @Test
    void getUsersPlayedMediaListShouldReturnList() {
        User user = new User("freddan");

        List<PlayedMedia> playedMediaList = Arrays.asList(new PlayedMedia("music", "title", "url", "release"));
        user.setPlayedMedia(playedMediaList);

        List<PlayedMedia> response = userService.getUsersPlayedMediaList(user);

        assertEquals(playedMediaList, response, "ERROR: Lists was not identical");
    }

    @Test
    void getUsersPlayedGenreListShouldReturnList() {
        User user = new User("freddan");

        List<PlayedGenre> playedGenreList = Arrays.asList(new PlayedGenre("rock", "music"));
        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersPlayedGenreList(user);

        assertEquals(playedGenreList, response, "ERROR: Lists was not identical");
    }

    @Test
    void getMediaFromUsersMediaListShouldReturnPlayedMedia() {
        User user = new User("freddan");

        String url = "url";

        List<PlayedMedia> playedMediaList = Arrays.asList(new PlayedMedia("music", "title", url, "release"));
        user.setPlayedMedia(playedMediaList);

        PlayedMedia response = userService.getMediaFromUsersMediaList(user, url);

        assertEquals("title", response.getTitle(), "ERROR: Titles was not identical");
    }

    @Test
    void getMediaFromUsersMediaListShouldReturnException() {
        User user = new User("freddan");

        String url = "non existing";

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            userService.getMediaFromUsersMediaList(user, url);
        }, "ERROR: Exception was not thrown");

        assertEquals("ERROR: not found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");
    }

    @Test
    void getMusicByUrlShouldReturnMusic() {
        Music music = new Music("music", "title", "url", "release");

        when(musicServiceMock.findMusicByUrl("url")).thenReturn(music);

        Music response = userService.getMusicByUrl("url");

        assertEquals(music, response, "ERROR: Objects was not identical");

        verify(musicServiceMock).findMusicByUrl("url");
    }

    @Test
    void getPodByUrl() {
        Pod pod = new Pod("pod", "title", "url", "release");

        when(podServiceMock.findPodByUrl("url")).thenReturn(pod);

        Pod response = userService.getPodByUrl("url");

        assertEquals(pod, response, "ERROR: Objects was not identical");

        verify(podServiceMock).findPodByUrl("url");
    }

    @Test
    void getVideoByUrl() {
        Video video = new Video("video", "title", "url", "release");

        when(videoServiceMock.findVideoByUrl("url")).thenReturn(video);

        Video response = userService.getVideoByUrl("url");

        assertEquals(video, response, "ERROR: Objects was not identical");

        verify(videoServiceMock).findVideoByUrl("url");
    }

    @Test
    void videoRecommendationsShouldReturnList() {
        User user = new User("freddan");
        long id = 1;
        user.setId(id);

        List<Video> videoList = Arrays.asList(
                new Video("video", "title", "url", "release"),
                new Video("video", "title2", "url2", "release2"),
                new Video("video", "title3", "url3", "release3")
        );

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        UserService userServiceSpy = spy(userService);

        doReturn(videoList).when(userServiceSpy).totalTop10Videos(user);

        List<Video> response = userServiceSpy.videoRecommendations(id);

        assertEquals(videoList, response, "ERROR: Lists was not identical");

        verify(userRepositoryMock).findById(id);
        verify(userServiceSpy).totalTop10Videos(user);
    }

    @Test
    void podRecommendations() {
        User user = new User("freddan");
        long id = 1;
        user.setId(id);

        List<Pod> podList = Arrays.asList(
                new Pod("pod", "title", "url", "release"),
                new Pod("pod", "title2", "url2", "release2"),
                new Pod("pod", "title3", "url3", "release3")
        );

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        UserService userServiceSpy = spy(userService);

        doReturn(podList).when(userServiceSpy).totalTop10Pods(user);

        List<Pod> response = userServiceSpy.podRecommendations(id);

        assertEquals(podList, response, "ERROR: Lists was not identical");

        verify(userRepositoryMock).findById(id);
        verify(userServiceSpy).podRecommendations(id);
    }

    @Test
    void musicRecommendations() {
        User user = new User("freddan");
        long id = 1;
        user.setId(id);

        List<Music> musicList = Arrays.asList(
                new Music("music", "title", "url", "release"),
                new Music("music", "title2", "url2", "release2"),
                new Music("music", "title3", "url3", "release3")
        );

        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(user));

        UserService userServiceSpy = spy(userService);

        doReturn(musicList).when(userServiceSpy).totalTop10Songs(user);

        List<Music> response = userServiceSpy.musicRecommendations(id);

        assertEquals(musicList, response, "ERROR: Lists was not identical");

        verify(userRepositoryMock).findById(id);
        verify(userServiceSpy).totalTop10Songs(user);
    }

    @Test
    void getUsersMostPlayedGenresLessThanThreeMusicSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        rock.setTotalPlays(20);
        jazz.setTotalPlays(10);

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "music");

        assertEquals("rock", response.get(0).getGenre(), "ERROR: Rock was not the most played genre");
        assertEquals("jazz", response.get(1).getGenre(), "ERROR: Jazz was not the second most played genre");
    }

    @Test
    void getUsersMostPlayedGenresMoreThanThreeMusicSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        PlayedGenre hiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre pop = new PlayedGenre("pop", "music");
        rock.setTotalPlays(20);
        jazz.setTotalPlays(10);
        hiphop.setTotalPlays(5);
        pop.setTotalPlays(8);

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz, hiphop, pop);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "music");

        assertEquals(3, response.size(), "ERROR: It's not top 3");
        assertEquals("rock", response.get(0).getGenre(), "ERROR: Rock was not the most played genre");
        assertEquals("jazz", response.get(1).getGenre(), "ERROR: Jazz was not the second most played genre");
        assertEquals("pop", response.get(2).getGenre(), "ERROR: Pop was not the third most played genre");
    }

    @Test
    void getUsersMostPlayedGenresMoreThanThreeWithLikedGenreMusicSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        PlayedGenre hiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre pop = new PlayedGenre("pop", "music");
        rock.setTotalPlays(20);
        jazz.setTotalPlays(10);
        hiphop.setTotalPlays(5);
        pop.setTotalPlays(8);
        pop.likeGenre();

        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz, hiphop, pop);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "music");

        assertEquals(1, response.size(), "ERROR: It's not top 3");
        assertEquals("pop", response.get(0).getGenre(), "ERROR: Pop was not the third most played genre");
    }

    @Test
    void getUsersMostPlayedGenresLessThanThreePodSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre business = new PlayedGenre("business", "pod");
        PlayedGenre coffee = new PlayedGenre("coffee", "pod");
        business.setTotalPlays(20);
        coffee.setTotalPlays(10);

        List<PlayedGenre> playedGenreList = Arrays.asList(business, coffee);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "pod");

        assertEquals("business", response.get(0).getGenre(), "ERROR: Business was not the most played genre");
        assertEquals("coffee", response.get(1).getGenre(), "ERROR: Coffee was not the second most played genre");
    }

    @Test
    void getUsersMostPlayedGenresMoreThanThreePodSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre business = new PlayedGenre("business", "pod");
        PlayedGenre coffee = new PlayedGenre("coffee", "pod");
        PlayedGenre food = new PlayedGenre("food", "pod");
        PlayedGenre snus = new PlayedGenre("snus", "pod");
        business.setTotalPlays(20);
        coffee.setTotalPlays(10);
        food.setTotalPlays(5);
        snus.setTotalPlays(8);

        List<PlayedGenre> playedGenreList = Arrays.asList(business, coffee, food, snus);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "pod");

        assertEquals(3, response.size(), "ERROR: It's not top 3");
        assertEquals("business", response.get(0).getGenre(), "ERROR: Business was not the most played genre");
        assertEquals("coffee", response.get(1).getGenre(), "ERROR: Coffee was not the second most played genre");
        assertEquals("snus", response.get(2).getGenre(), "ERROR: Snus was not the third most played genre");
    }

    @Test
    void getUsersMostPlayedGenresLessThanThreeVideoSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre action = new PlayedGenre("action", "video");
        PlayedGenre romance = new PlayedGenre("romance", "video");
        action.setTotalPlays(20);
        romance.setTotalPlays(10);

        List<PlayedGenre> playedGenreList = Arrays.asList(action, romance);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "video");

        assertEquals("action", response.get(0).getGenre(), "ERROR: Actiopn was not the most played genre");
        assertEquals("romance", response.get(1).getGenre(), "ERROR: Romance was not the second most played genre");
    }

    @Test
    void getUsersMostPlayedGenresMoreThanThreeVideoSortedByPlays() {
        User user = new User("freddan");

        PlayedGenre action = new PlayedGenre("action", "video");
        PlayedGenre romance = new PlayedGenre("romance", "video");
        PlayedGenre comedy = new PlayedGenre("comedy", "video");
        PlayedGenre horror = new PlayedGenre("horror", "video");
        action.setTotalPlays(20);
        romance.setTotalPlays(10);
        comedy.setTotalPlays(5);
        horror.setTotalPlays(8);

        List<PlayedGenre> playedGenreList = Arrays.asList(action, romance, comedy, horror);

        user.setPlayedGenre(playedGenreList);

        List<PlayedGenre> response = userService.getUsersMostPlayedGenresSortedByPlays(user, "video");

        assertEquals(3, response.size(), "ERROR: It's not top 3");
        assertEquals("action", response.get(0).getGenre(), "ERROR: Action was not the most played genre");
        assertEquals("romance", response.get(1).getGenre(), "ERROR: Romance was not the second most played genre");
        assertEquals("horror", response.get(2).getGenre(), "ERROR: Horror was not the third most played genre");
    }

    @Test
    void sortAllPlayedGenresByPlaysMusicShouldReturnSortedList() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        PlayedGenre hiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre pop = new PlayedGenre("pop", "music");

        rock.setTotalPlays(12);
        jazz.setTotalPlays(90);
        hiphop.setTotalPlays(15);
        pop.setTotalPlays(60);

        List<PlayedGenre> list = Arrays.asList(rock, jazz, hiphop, pop);

        user.setPlayedGenre(list);

        List<PlayedGenre> response = userService.sortAllPlayedGenresByPlays(user, "music");

        assertEquals("jazz", response.get(0).getGenre(), "ERROR: Jazz was not the most played genre");
        assertEquals("pop", response.get(1).getGenre(), "ERROR: Pop was not the second most played genre");
        assertEquals("hiphop", response.get(2).getGenre(), "ERROR: Hiphop was not the third most played genre");
        assertEquals("rock", response.get(3).getGenre(), "ERROR: Rock was not the least played genre");
    }

    @Test
    void sortAllPlayedGenresWithLikedGenreByPlaysMusicShouldReturnSortedList() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        PlayedGenre hiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre pop = new PlayedGenre("pop", "music");

        rock.setTotalPlays(12);
        jazz.setTotalPlays(90);
        hiphop.setTotalPlays(15);
        pop.setTotalPlays(60);
        pop.likeGenre();

        List<PlayedGenre> list = Arrays.asList(rock, jazz, hiphop, pop);

        user.setPlayedGenre(list);

        List<PlayedGenre> response = userService.sortAllPlayedGenresByPlays(user, "music");

        assertEquals("pop", response.get(0).getGenre(), "ERROR: Pop was not a liked played genre");
    }

    @Test
    void sortAllPlayedGenresWithDislikedGenreByPlaysMusicShouldReturnSortedList() {
        User user = new User("freddan");

        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        PlayedGenre hiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre pop = new PlayedGenre("pop", "music");

        rock.setTotalPlays(12);
        jazz.setTotalPlays(90);
        hiphop.setTotalPlays(15);
        pop.setTotalPlays(60);
        jazz.disLikeGenre();

        List<PlayedGenre> list = Arrays.asList(rock, jazz, hiphop, pop);

        user.setPlayedGenre(list);

        List<PlayedGenre> response = userService.sortAllPlayedGenresByPlays(user, "music");

        assertEquals("pop", response.get(0).getGenre(), "ERROR: Pop was not a liked played genre");
        assertEquals(3, response.size(), "ERROR: Size was not 3");
    }

    @Test
    void sortAllPlayedGenresByPlaysPodShouldReturnSortedList() {
        User user = new User("freddan");

        PlayedGenre business = new PlayedGenre("business", "pod");
        PlayedGenre space = new PlayedGenre("space", "pod");
        PlayedGenre environment = new PlayedGenre("environment", "pod");
        PlayedGenre coffee = new PlayedGenre("coffee", "pod");

        business.setTotalPlays(12);
        space.setTotalPlays(90);
        environment.setTotalPlays(15);
        coffee.setTotalPlays(60);

        List<PlayedGenre> list = Arrays.asList(business, space, environment, coffee);

        user.setPlayedGenre(list);

        List<PlayedGenre> response = userService.sortAllPlayedGenresByPlays(user, "pod");

        assertEquals("space", response.get(0).getGenre(), "ERROR: Space was not the most played genre");
        assertEquals("coffee", response.get(1).getGenre(), "ERROR: Coffee was not the second most played genre");
        assertEquals("environment", response.get(2).getGenre(), "ERROR: Environment was not the third most played genre");
        assertEquals("business", response.get(3).getGenre(), "ERROR: Business was not the least played genre");
    }

    @Test
    void sortAllPlayedGenresByPlaysVideoShouldReturnSortedList() {
        User user = new User("freddan");

        PlayedGenre action = new PlayedGenre("action", "video");
        PlayedGenre romance = new PlayedGenre("romance", "video");
        PlayedGenre comedy = new PlayedGenre("comedy", "video");
        PlayedGenre horror = new PlayedGenre("horror", "video");

        action.setTotalPlays(12);
        romance.setTotalPlays(90);
        comedy.setTotalPlays(15);
        horror.setTotalPlays(60);

        List<PlayedGenre> list = Arrays.asList(action, romance, comedy, horror);

        user.setPlayedGenre(list);

        List<PlayedGenre> response = userService.sortAllPlayedGenresByPlays(user, "video");

        assertEquals("romance", response.get(0).getGenre(), "ERROR: Romance was not the most played genre");
        assertEquals("horror", response.get(1).getGenre(), "ERROR: Horror was not the second most played genre");
        assertEquals("comedy", response.get(2).getGenre(), "ERROR: Comedy was not the third most played genre");
        assertEquals("action", response.get(3).getGenre(), "ERROR: Action was not the least played genre");
    }

    @Test
    void sortGenresByPlaysShouldReturnSortedList() {
        Genre jazz = new Genre("jazz", "music");
        Genre rock = new Genre("rock", "music");
        Genre hiphop = new Genre("hiphop", "music");
        Genre pop = new Genre("pop", "music");
        Genre rnb = new Genre("rnb", "music");

        jazz.setTotalPlays(10);
        rock.setTotalPlays(20);
        hiphop.setTotalPlays(15);
        pop.setTotalPlays(21);
        rnb.setTotalPlays(16);

        List<Genre> genreList = Arrays.asList(jazz, rock, hiphop, pop, rnb);

        List<Genre> response = userService.sortGenresByPlays(genreList);

        assertEquals("pop", response.get(0).getGenre(), "ERROR: Pop was not the most played genre");
        assertEquals("rock", response.get(1).getGenre(), "ERROR: Rock was not the second most played genre");
        assertEquals("rnb", response.get(2).getGenre(), "ERROR: RNB was not the third most played genre");
        assertEquals("hiphop", response.get(3).getGenre(), "ERROR: Hiphop was not the fourth most played genre");
        assertEquals("jazz", response.get(4).getGenre(), "ERROR: Jazz was not the least played genre");
    }

    @Test
    void sortAllMusicByPlays() {
        Music music = new Music("music", "title", "url", "release");
        Music music2 = new Music("music", "title2", "url2", "release2");
        Music music3 = new Music("music", "title3", "url3", "release3");
        music.setPlayCounter(4);
        music2.setPlayCounter(5);
        music3.setPlayCounter(6);

        List<Music> musicList = Arrays.asList(music, music2, music3);

        List<Music> response = userService.sortAllMusicByPlays(musicList);

        assertEquals(3, response.size(), "ERROR: Sizes was not identical");
        assertEquals("title3", response.get(0).getTitle(), "ERROR: title 3 was not the most played song");
    }

    @Test
    void sortAllVideosByPlays() {
        Video video = new Video("video", "title", "url", "release");
        Video video2 = new Video("video", "title2", "url2", "release2");
        Video video3 = new Video("video", "title3", "url3", "release3");
        video.setPlayCounter(4);
        video2.setPlayCounter(5);
        video3.setPlayCounter(6);

        List<Video> videoList = Arrays.asList(video, video2, video3);

        List<Video> response = userService.sortAllVideosByPlays(videoList);

        assertEquals(3, response.size(), "ERROR: Sizes was not identical");
        assertEquals("title3", response.get(0).getTitle(), "ERROR: title 3 was not the most viewed video");
    }

    @Test
    void sortAllPodsByPlays() {
        Pod pod = new Pod("pod", "title", "url", "release");
        Pod pod2 = new Pod("pod", "title2", "url2", "release2");
        Pod pod3 = new Pod("pod", "title3", "url3", "release3");
        pod.setPlayCounter(4);
        pod2.setPlayCounter(5);
        pod3.setPlayCounter(6);

        List<Pod> podList = Arrays.asList(pod, pod2, pod3);

        List<Pod> response = userService.sortAllPodsByPlays(podList);

        assertEquals(3, response.size(), "ERROR: Sizes was not identical");
        assertEquals("title3", response.get(0).getTitle(), "ERROR: title 3 was not the most viewed pod");
    }

    @Test
    void convertUserPlayedGenresToGenreShouldReturnList() {
        Genre rock = new Genre("rock", "music");
        Genre jazz = new Genre("jazz", "music");
        Genre hiphop = new Genre("hiphop", "music");
        Genre pop = new Genre("pop", "music");

        PlayedGenre playedRock = new PlayedGenre("rock", "music");
        PlayedGenre playedJazz = new PlayedGenre("jazz", "music");
        PlayedGenre playedHiphop = new PlayedGenre("hiphop", "music");
        PlayedGenre playedPop = new PlayedGenre("pop", "music");
        List<PlayedGenre> playedGenreList = Arrays.asList(playedRock, playedJazz, playedHiphop, playedPop);

        when(genreServiceMock.findGenreByGenre("rock")).thenReturn(rock);
        when(genreServiceMock.findGenreByGenre("jazz")).thenReturn(jazz);
        when(genreServiceMock.findGenreByGenre("hiphop")).thenReturn(hiphop);
        when(genreServiceMock.findGenreByGenre("pop")).thenReturn(pop);

        List<Genre> response = userService.convertUserPlayedGenresToGenre(playedGenreList);

        assertEquals("rock", response.get(0).getGenre(), "ERROR: Rock was not the first converted genre in the list");
    }

    // TODO
    @Test
    void totalTop10VideosAllVideosOver10ShouldReturnList() {
        User user = new User("freddan");

        PlayedMedia playedMedia = new PlayedMedia("video", "title", "url", "release");
        playedMedia.setTimesPlayed(1);
        List<PlayedMedia> usersPlayedVideos = Arrays.asList(playedMedia);
        user.setPlayedMedia(usersPlayedVideos);

        Video video1 = new Video("video", "title", "url", "release");
        Video video2 = new Video("video", "title2", "url2", "release2");
        Video video3 = new Video("video", "title3", "url3", "release3");
        Video video4 = new Video("video", "title4", "url4", "release4");
        Video video5 = new Video("video", "title5", "url5", "release5");
        Video video6 = new Video("video", "title6", "url6", "release6");
        Video video7 = new Video("video", "title7", "url7", "release7");
        Video video8 = new Video("video", "title8", "url8", "release8");
        Video video9 = new Video("video", "title9", "url9", "release9");
        Video video10 = new Video("video", "title10", "url10", "release10");
        Video video11 = new Video("video", "title11", "url11", "release11");

        video8.setPlayCounter(30);
        video1.setPlayCounter(20);
        video2.setPlayCounter(15);
        video3.setPlayCounter(10);
        video4.setPlayCounter(9);
        video5.setPlayCounter(8);
        video6.setPlayCounter(7);
        video7.setPlayCounter(6);
        video9.setPlayCounter(5);
        video10.setPlayCounter(4);
        video11.setPlayCounter(3);

        List<Video> allVideos = new ArrayList<>(Arrays.asList(video1, video2, video3, video4, video5, video6, video7, video8, video9, video10, video11));

        when(videoServiceMock.findAllVideos()).thenReturn(allVideos);

        List<Video> response = userService.totalTop10Videos(user);

        assertEquals(30, response.get(0).getPlayCounter(), "ERROR: Video 8 was not in the top");
        assertEquals(10, response.size(), "ERROR: Size was not 8");
    }

    @Test
    void totalTop10VideosAllVideosOver1ButLessThan10ShouldReturnList() {

    }

    @Test
    void totalTop10VideosAllVideosIsEmptyShouldReturnList() {
    }

    @Test
    void totalTop10PodsAllPodsOver10ShouldReturnList() {
        User user = new User("freddan");

        PlayedMedia playedMedia = new PlayedMedia("pod", "title", "url", "release");
        playedMedia.setTimesPlayed(1);
        List<PlayedMedia> usersPlayedVideos = Arrays.asList(playedMedia);
        user.setPlayedMedia(usersPlayedVideos);

        Pod pod1 = new Pod("pod", "title", "url", "release");
        Pod pod2 = new Pod("pod", "title2", "url2", "release2");
        Pod pod3 = new Pod("pod", "title3", "url3", "release3");
        Pod pod4 = new Pod("pod", "title4", "url4", "release4");
        Pod pod5 = new Pod("pod", "title5", "url5", "release5");
        Pod pod6 = new Pod("pod", "title6", "url6", "release6");
        Pod pod7 = new Pod("pod", "title7", "url7", "release7");
        Pod pod8 = new Pod("pod", "title8", "url8", "release8");
        Pod pod9 = new Pod("pod", "title9", "url9", "release9");
        Pod pod10 = new Pod("pod", "title10", "url10", "release10");
        Pod pod11 = new Pod("pod", "title11", "url11", "release11");

        pod8.setPlayCounter(30);
        pod1.setPlayCounter(20);
        pod2.setPlayCounter(15);
        pod3.setPlayCounter(10);
        pod4.setPlayCounter(9);
        pod5.setPlayCounter(8);
        pod6.setPlayCounter(7);
        pod7.setPlayCounter(6);
        pod9.setPlayCounter(5);
        pod10.setPlayCounter(4);
        pod11.setPlayCounter(3);

        List<Pod> allPods = new ArrayList<>(Arrays.asList(pod1, pod2, pod3, pod4, pod5, pod6, pod7, pod8, pod9, pod10, pod11));

        when(podServiceMock.findAllPods()).thenReturn(allPods);

        List<Pod> response = userService.totalTop10Pods(user);

        assertEquals(30, response.get(0).getPlayCounter(), "ERROR: Pod 8 was not in the top");
        assertEquals(10, response.size(), "ERROR: Size was not 8");
    }

    @Test
    void totalTop10PodsAllPodsOver1ButLessThan10ShouldReturnList() {

    }

    @Test
    void totalTop10PodsAllPodsIsEmptyShouldReturnList() {

    }

    @Test
    void totalTop10Songs() {
    }

    @Test
    void getTop8SongsFromUsersTopGenres() {
    }

    @Test
    void getUnlistenedGenresOfTypeMusicShouldReturnList() {
        String type = "music";

        Genre rockGenre = new Genre("rock", type);
        Genre jazzGenre = new Genre("jazz", type);
        Genre hiphopGenre = new Genre("hiphop", type);
        Genre popGenre = new Genre("pop", type);
        Genre nonMusicGenre = new Genre("delete", "pod");
        rockGenre.setTotalPlays(1);
        jazzGenre.setTotalPlays(2);
        hiphopGenre.setTotalPlays(3);
        popGenre.setTotalPlays(4);
        nonMusicGenre.setTotalPlays(5);
        List<Genre> genres = new ArrayList<>(Arrays.asList(rockGenre, jazzGenre, hiphopGenre, popGenre, nonMusicGenre));

        when(genreServiceMock.getAllGenres()).thenReturn(genres);

        User user = new User("freddan");


        PlayedGenre rock = new PlayedGenre("rock", type);
        PlayedGenre jazz = new PlayedGenre("jazz", type);
        rock.setTotalPlays(1);
        jazz.setTotalPlays(2);
        List<PlayedGenre> genreList = Arrays.asList(rock, jazz);

        user.setPlayedGenre(genreList);

        List<Genre> unlistenedGenres = userService.getUnlistenedGenres(user, type);

        assertEquals(2, unlistenedGenres.size(), "ERROR: Size was not 2");

        verify(genreServiceMock).getAllGenres();
    }

    @Test
    void getUnlistenedGenresOfTypePodShouldReturnList() {
        String type = "pod";

        Genre businessGenre = new Genre("business", type);
        Genre travelGenre = new Genre("travel", type);
        Genre environmentGenre = new Genre("environmentGenre", type);
        Genre coffeeGenre = new Genre("coffee", type);
        Genre nonPodGenre = new Genre("delete", "music");
        businessGenre.setTotalPlays(1);
        travelGenre.setTotalPlays(2);
        environmentGenre.setTotalPlays(3);
        coffeeGenre.setTotalPlays(4);
        nonPodGenre.setTotalPlays(5);
        List<Genre> genres = new ArrayList<>(Arrays.asList(businessGenre, travelGenre, environmentGenre, coffeeGenre, nonPodGenre));

        when(genreServiceMock.getAllGenres()).thenReturn(genres);

        User user = new User("freddan");


        PlayedGenre business = new PlayedGenre("business", type);
        PlayedGenre travel = new PlayedGenre("travel", type);
        business.setTotalPlays(1);
        travel.setTotalPlays(2);
        List<PlayedGenre> genreList = Arrays.asList(business, travel);

        user.setPlayedGenre(genreList);

        List<Genre> unlistenedGenres = userService.getUnlistenedGenres(user, type);

        assertEquals(2, unlistenedGenres.size(), "ERROR: Size was not 2");

        verify(genreServiceMock).getAllGenres();
    }

    @Test
    void getUnlistenedGenresOfTypeVideoShouldReturnList() {
        String type = "video";

        Genre actionGenre = new Genre("action", type);
        Genre comedyGenre = new Genre("comedy", type);
        Genre romanceGenre = new Genre("romanceGenre", type);
        Genre horrorGenre = new Genre("horror", type);
        Genre nonVideoGenre = new Genre("delete", "music");
        actionGenre.setTotalPlays(1);
        comedyGenre.setTotalPlays(2);
        romanceGenre.setTotalPlays(3);
        horrorGenre.setTotalPlays(4);
        nonVideoGenre.setTotalPlays(5);
        List<Genre> genres = new ArrayList<>(Arrays.asList(actionGenre, comedyGenre, romanceGenre, horrorGenre, nonVideoGenre));

        when(genreServiceMock.getAllGenres()).thenReturn(genres);

        User user = new User("freddan");


        PlayedGenre action = new PlayedGenre("action", type);
        PlayedGenre comedy = new PlayedGenre("comedy", type);
        action.setTotalPlays(1);
        comedy.setTotalPlays(2);
        List<PlayedGenre> genreList = Arrays.asList(action, comedy);

        user.setPlayedGenre(genreList);

        List<Genre> unlistenedGenres = userService.getUnlistenedGenres(user, type);

        assertEquals(2, unlistenedGenres.size(), "ERROR: Size was not 2");

        verify(genreServiceMock).getAllGenres();
    }
}