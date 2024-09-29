package UserService.userService.services;

import UserService.userService.repositories.MusicRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicServiceTest {
    private MusicRepository musicRepositoryMock;
    private MusicService musicService;

    @BeforeEach
    void setUp() {
        musicRepositoryMock = mock(MusicRepository.class);
        musicService = new MusicService(musicRepositoryMock);
    }

    @Test
    void findAllMusicShouldReturnList() {
        List<Music> musicList = Arrays.asList(
                new Music("music", "title", "url", "release"),
                new Music("music", "title2", "url2", "release2"),
                new Music("music", "title3", "url3", "release3")
        );

        when(musicRepositoryMock.findAll()).thenReturn(musicList);

        List<Music> response = musicService.findAllMusic();

        assertEquals(musicList, response, "ERROR: Lists was not identical");

        verify(musicRepositoryMock).findAll();
    }

    @Test
    void musicExistsByUrlShouldReturnTrue() {
        when(musicRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(true);

        boolean isTrue = musicService.musicExistsByUrl("url");

        assertTrue(isTrue, "ERROR: was False");

        verify(musicRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void musicExistsByUrlShouldReturnFalse() {
        when(musicRepositoryMock.existsByUrlIgnoreCase("url")).thenReturn(false);

        boolean isFalse = musicService.musicExistsByUrl("url");

        assertFalse(isFalse, "ERROR: was True");

        verify(musicRepositoryMock).existsByUrlIgnoreCase("url");
    }

    @Test
    void findMusicByUrlShouldReturnMusic() {
        Music music = new Music("music", "title", "url", "release");
        musicRepositoryMock.save(music);

        when(musicRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.of(music));

        Music response = musicService.findMusicByUrl("url");

        assertEquals(music, response, "ERROR: Objects was not identical");

        verify(musicRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void findMusicByUrlShouldReturnException() {
        when(musicRepositoryMock.findByUrlIgnoreCase("url")).thenReturn(Optional.empty());

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> {
            musicService.findMusicByUrl("url");
        }, "ERROR: Exception was not thronw");

        assertEquals("ERROR: Could not be found", response.getReason(), "ERROR: Exceptions was not identical");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "ERROR: Status Codes was not identical");

        verify(musicRepositoryMock).findByUrlIgnoreCase("url");
    }

    @Test
    void findAllMusicInGenreShouldReturnList() {
        List<Music> music = Arrays.asList(
                new Music("music", "title", "url", "release"),
                new Music("music", "title2", "url2", "release2"),
                new Music("music", "title3", "url3", "release3")
        );

        Genre rock = new Genre("rock", "music");

        when(musicRepositoryMock.findMusicByGenres(rock)).thenReturn(music);

        List<Music> response = musicService.findAllMusicInGenre(rock);

        assertEquals(music, response, "ERROR: Lists was not identical");
        assertEquals(3, response.size(), "ERROR: Sizes was not identical");

        verify(musicRepositoryMock).findMusicByGenres(rock);
    }

    @Test
    void addPlayShouldReturnMusic() {
        Music music = new Music("music", "title", "url", "release");
        musicRepositoryMock.save(music);

        when(musicRepositoryMock.save(music)).thenReturn(music);

        Music response = musicService.addPlay(music);

        assertEquals(1, music.getPlayCounter(), "ERROR: PlayCounter was not identical");
        assertEquals(music, response, "ERROR: Objects was not identical");

        verify(musicRepositoryMock, times(2)).save(music);
    }
}