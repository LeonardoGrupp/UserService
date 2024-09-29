package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
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
    void likeMedia() {
    }

    @Test
    void likeGenre() {
    }

    @Test
    void disLikeMedia() {
    }

    @Test
    void disLikeGenre() {
    }

    @Test
    void resetLikesAndDisLikesOfMedia() {
    }

    @Test
    void resetLikesAndDisLikesOfGenre() {
    }

    @Test
    void findMusicByUrl() {
    }

    @Test
    void isMusic() {
    }

    @Test
    void isPod() {
    }

    @Test
    void isVideo() {
    }

    @Test
    void hasPlayedMediaBefore() {
    }

    @Test
    void hasPlayedMusicGenreBefore() {
    }

    @Test
    void hasPlayedPodGenreBefore() {
    }

    @Test
    void hasPlayedVideoGenreBefore() {
    }

    @Test
    void getUsersPlayedMediaList() {
    }

    @Test
    void getUsersPlayedGenreList() {
    }

    @Test
    void getMediaFromUsersMediaList() {
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
    void videoRecommendations() {
    }

    @Test
    void podRecommendations() {
    }

    @Test
    void musicRecommendations() {
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
    }

    @Test
    void sortAllVideosByPlays() {
    }

    @Test
    void sortAllPodsByPlays() {
    }

    @Test
    void convertUserPlayedGenresToGenre() {
    }

    @Test
    void totalTop10Videos() {
    }

    @Test
    void totalTop10Pods() {
    }

    @Test
    void totalTop10Songs() {
    }

    @Test
    void getTop8SongsFromUsersTopGenres() {
    }

    @Test
    void getUnlistenedGenres() {
    }
}