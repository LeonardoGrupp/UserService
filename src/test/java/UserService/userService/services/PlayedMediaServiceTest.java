package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.repositories.PlayedMediaRepository;
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

class PlayedMediaServiceTest {

    private PlayedMediaRepository playedMediaRepositoryMock;
    private PlayedMediaService playedMediaService;

    @BeforeEach
    void setUp() {
        playedMediaRepositoryMock = mock(PlayedMediaRepository.class);
        playedMediaService = new PlayedMediaService(playedMediaRepositoryMock);
    }

    @Test
    void allPlayedMediaShouldReturnList() {
        List<PlayedMedia> playedMediaList = Arrays.asList(
                new PlayedMedia("music", "title", "url", "release"),
                new PlayedMedia("music", "title2", "url2", "release2"),
                new PlayedMedia("music", "title3", "url3", "release3")
        );

        when(playedMediaRepositoryMock.findAll()).thenReturn(playedMediaList);

        List<PlayedMedia> response = playedMediaService.allPlayedMedia();

        assertEquals(playedMediaList, response, "ERROR: Lists was not identical");

        verify(playedMediaRepositoryMock).findAll();
    }

    @Test
    void createShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");

        when(playedMediaRepositoryMock.save(playedMedia)).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.create(playedMedia);

        assertEquals(playedMedia, response, "ERROR: Objects was not identical");

        verify(playedMediaRepositoryMock).save(playedMedia);
    }

    @Test
    void createMusicFromUserShouldReturnPlayedMedia() {
        Music music = new Music("music", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate());
        playedMediaRepositoryMock.save(playedMedia);



        when(playedMediaRepositoryMock.save(any(PlayedMedia.class))).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createMusicFromUser(music);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");

        verify(playedMediaRepositoryMock, times(2)).save(any(PlayedMedia.class));
    }

    @Test
    void createMusicFromUserWithListShouldReturnPlayedMedia() {
        Music music = new Music("music", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        PlayedGenre rock = new PlayedGenre("rock", "music");
        PlayedGenre jazz = new PlayedGenre("jazz", "music");
        List<PlayedGenre> playedGenreList = Arrays.asList(rock, jazz);

        playedMedia.setGenres(playedGenreList);
        playedMediaRepositoryMock.save(playedMedia);

        when(playedMediaRepositoryMock.save(playedMedia)).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createMusicFromUserWithList(music, playedGenreList);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");
        assertEquals("rock", response.getGenres().get(0).getGenre(), "ERROR: Genre was not identical");

        verify(playedMediaRepositoryMock).save(playedMedia);
    }

    @Test
    void createPodFromUserShouldReturnPlayedMedia() {
        Pod pod = new Pod("pod", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate());
        playedMediaRepositoryMock.save(playedMedia);



        when(playedMediaRepositoryMock.save(any(PlayedMedia.class))).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createPodFromUser(pod);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");

        verify(playedMediaRepositoryMock, times(2)).save(any(PlayedMedia.class));
    }

    @Test
    void createPodFromUserWithListShouldReturnPlayedMedia() {
        Pod pod = new Pod("pod", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia("pod", "title", "url", "release");
        PlayedGenre business = new PlayedGenre("business", "pod");
        PlayedGenre politics = new PlayedGenre("politics", "pod");
        List<PlayedGenre> playedGenreList = Arrays.asList(business, politics);

        playedMedia.setGenres(playedGenreList);
        playedMediaRepositoryMock.save(playedMedia);

        when(playedMediaRepositoryMock.save(playedMedia)).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createPodFromUserWithList(pod, playedGenreList);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");
        assertEquals("business", response.getGenres().get(0).getGenre(), "ERROR: Genre was not identical");

        verify(playedMediaRepositoryMock).save(playedMedia);
    }

    @Test
    void createVideoFromUserShouldReturnPlayedMedia() {
        Video video = new Video("video", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate());
        playedMediaRepositoryMock.save(playedMedia);



        when(playedMediaRepositoryMock.save(any(PlayedMedia.class))).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createVideoFromUser(video);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");

        verify(playedMediaRepositoryMock, times(2)).save(any(PlayedMedia.class));
    }

    @Test
    void createVideoFromUserWithList() {
        Video video = new Video("video", "title", "url", "release");
        PlayedMedia playedMedia = new PlayedMedia("video", "title", "url", "release");
        PlayedGenre action = new PlayedGenre("action", "video");
        PlayedGenre comedy = new PlayedGenre("comedy", "video");
        List<PlayedGenre> playedGenreList = Arrays.asList(action, comedy);

        playedMedia.setGenres(playedGenreList);
        playedMediaRepositoryMock.save(playedMedia);

        when(playedMediaRepositoryMock.save(playedMedia)).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.createVideoFromUserWithList(video, playedGenreList);

        assertEquals(playedMedia.getTitle(), response.getTitle(), "ERROR: Titles was not identical");
        assertEquals("action", response.getGenres().get(0).getGenre(), "ERROR: Genre was not identical");

        verify(playedMediaRepositoryMock).save(playedMedia);
    }

    @Test
    void saveShouldReturnPlayedMedia() {
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");

        when(playedMediaRepositoryMock.save(playedMedia)).thenReturn(playedMedia);

        PlayedMedia response = playedMediaService.save(playedMedia);

        assertEquals(playedMedia, response, "ERROR: Objects was not identical");

        verify(playedMediaRepositoryMock).save(playedMedia);
    }

    @Test
    void deleteShouldReturnString() {
        String expected = "Deleted PlayedMedia item";
        long id = 1;
        PlayedMedia playedMedia = new PlayedMedia("music", "title", "url", "release");
        playedMedia.setId(1);

        when(playedMediaRepositoryMock.findById(id)).thenReturn(Optional.of(playedMedia));

        String response = playedMediaService.delete(id);

        assertEquals(expected, response, "ERROR: Strings was not identical");

        verify(playedMediaRepositoryMock).findById(id);
        verify(playedMediaRepositoryMock).delete(playedMedia);
    }

    @Test
    void deleteShouldReturnException() {
        String expected = "ERROR: PlayedMedia was not found";
        long id = 1;

        when(playedMediaRepositoryMock.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            playedMediaService.delete(id);
        }, "ERROR: Exception was not thrown");

        assertEquals(expected, response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Code was not identical");

        verify(playedMediaRepositoryMock).findById(id);
        verify(playedMediaRepositoryMock, never()).delete(any());
    }
}