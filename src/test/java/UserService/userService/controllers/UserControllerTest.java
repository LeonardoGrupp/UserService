package UserService.userService.controllers;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.services.UserService;
import UserService.userService.vo.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userServiceMock;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserService.class);
        userController = new UserController(userServiceMock);
    }

    @Test
    void getAllUsersShouldReturnList() {
        List<User> userList = Arrays.asList(
                new User("Freddan"),
                new User("Marcus"),
                new User("Tobias")
        );

        when(userServiceMock.findAllUsers()).thenReturn(userList);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(3, response.getBody().size(), "ERROR: Sizes was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).findAllUsers();
    }

    @Test
    void getUserByIdShouldReturnUser() {
        long userId = 1;
        User user = new User("Freddan");
        user.setId(userId);

        when(userServiceMock.findUserById(userId)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals("Freddan", response.getBody().getUsername(), "ERROR: Usernames was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).findUserById(userId);
    }

    @Test
    void getUserByUsernameShouldReturnUser() {
        User user = new User("Freddan");

        when(userServiceMock.findUserByUsername("Freddan")).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByUsername("Freddan");

        assertEquals(user, response.getBody(), "ERROR: Objects was not identical");
        assertEquals("Freddan", response.getBody().getUsername(), "ERROR: Usernames was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).findUserByUsername("Freddan");
    }

    @Test
    void createShouldReturnUser() {
        User user = new User("Freddan");

        when(userServiceMock.create(user)).thenReturn(user);

        ResponseEntity<User> response = userController.create(user);

        assertEquals(user, response.getBody(), "ERROR: Objects was not identical");
        assertEquals("Freddan", response.getBody().getUsername(), "ERROR: Usernames was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).create(user);
    }

    @Test
    void updateShouldReturnUser() {
        long userId = 1;
        User user = new User("Freddan");
        user.setId(userId);
        User newInfo = new User("Updated");

        when(userServiceMock.updateUser(userId, newInfo)).thenReturn(newInfo);

        ResponseEntity<User> response = userController.update(userId, newInfo);

        assertEquals("Updated", response.getBody().getUsername(), "ERROR: Usernames was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).updateUser(userId, newInfo);
    }

    @Test
    void deleteShouldReturnString() {
        String expected = "User successfully deleted";
        long userId = 1;

        when(userServiceMock.delete(userId)).thenReturn(expected);

        ResponseEntity<String> response = userController.delete(userId);

        assertEquals("User successfully deleted", response.getBody(), "ERROR: Strings was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).delete(userId);
    }

    @Test
    void playMediaShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        long userId = 1;

        when(userServiceMock.playMedia(userId, "url")).thenReturn(playedMedia);

        ResponseEntity<PlayedMedia> response = userController.playMedia(userId, "url");

        assertEquals("music", response.getBody().getType(), "ERROR: Types was not identical");
        assertEquals(playedMedia, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).playMedia(userId, "url");
    }

    @Test
    void likeMediaShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        long userId = 1;

        when(userServiceMock.likeMedia(userId, "url")).thenReturn(playedMedia);

        ResponseEntity<PlayedMedia> response = userController.likeMedia(userId, "url");

        assertEquals(playedMedia, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR Status Codes was not identical");

        verify(userServiceMock).likeMedia(userId, "url");
    }

    @Test
    void likeGenreShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        long userId = 1;

        when(userServiceMock.likeGenre(userId, "rock")).thenReturn(playedGenre);

        ResponseEntity<PlayedGenre> response = userController.likeGenre(userId, "rock");

        assertEquals(playedGenre, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).likeGenre(userId, "rock");
    }

    @Test
    void disLikeMediaShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        long userId = 1;

        when(userServiceMock.disLikeMedia(userId, "url")).thenReturn(playedMedia);

        ResponseEntity<PlayedMedia> response = userController.disLikeMedia(userId, "url");

        assertEquals(playedMedia, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).disLikeMedia(userId, "url");
    }

    @Test
    void disLikeGenreShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        long userId = 1;

        when(userServiceMock.disLikeGenre(userId, "rock")).thenReturn(playedGenre);

        ResponseEntity<PlayedGenre> response = userController.disLikeGenre(userId, "rock");

        assertEquals(playedGenre, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).disLikeGenre(userId, "rock");
    }

    @Test
    void resetLikesAndDisLikesShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        long userId = 1;

        when(userServiceMock.resetLikesAndDisLikesOfMedia(userId, "url")).thenReturn(playedMedia);

        ResponseEntity<PlayedMedia> response = userController.resetLikesAndDisLikes(userId, "url");

        assertEquals(playedMedia, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).resetLikesAndDisLikesOfMedia(userId, "url");
    }

    @Test
    void resetLikesAndDisLikesGenreShouldReturnPlayedGenre() {
        PlayedGenre playedGenre = new PlayedGenre("rock", "music");
        long userId = 1;

        when(userServiceMock.resetLikesAndDisLikesOfGenre(userId, "rock")).thenReturn(playedGenre);

        ResponseEntity<PlayedGenre> response = userController.resetLikesAndDisLikesGenre(userId, "rock");

        assertEquals(playedGenre, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).resetLikesAndDisLikesOfGenre(userId, "rock");
    }

    @Test
    void findMusicByUrlShouldReturnMusic() {
        Music music = new Music("music", "title", "url", "release");

        when(userServiceMock.findMusicByUrl("url")).thenReturn(music);

        ResponseEntity<Music> response = userController.findMusicByUrl("url");

        assertEquals(music, response.getBody(), "ERROR: Objects was not identical");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(userServiceMock).findMusicByUrl("url");
    }
}