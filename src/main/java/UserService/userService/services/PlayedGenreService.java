package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.User;
import UserService.userService.repositories.PlayedGenreRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import UserService.userService.vo.VideoGenre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayedGenreService {

    private PlayedGenreRepository playedGenreRepository;

    @Autowired
    public PlayedGenreService(PlayedGenreRepository playedGenreRepository) {
        this.playedGenreRepository = playedGenreRepository;
    }

    public List<PlayedGenre> allPlayedGenres() {
        return playedGenreRepository.findAll();
    }

    public PlayedGenre findPlayedGenreByName(String genre) {
        return playedGenreRepository.findPlayedGenreByGenre(genre);
    }

    public PlayedGenre create(PlayedGenre playedGenre) {
        return playedGenreRepository.save(playedGenre);
    }

    public PlayedGenre createFromMusicGenres(Genre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre()));
    }

    public PlayedGenre createFromPodGenres(Genre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre()));
    }

    public PlayedGenre createFromVideoGenres(VideoGenre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre()));
    }

    public List<PlayedGenre> createFromMusic(Music music) {
        List<PlayedGenre> playedGenreList = new ArrayList<>();

        for (Genre genre : music.getGenres()) {
            PlayedGenre playedGenre = new PlayedGenre(genre.getGenre());

            playedGenreRepository.save(playedGenre);
            playedGenreList.add(playedGenre);
        }

        return playedGenreList;
    }

    public PlayedGenre save(PlayedGenre playedGenre) {
        return playedGenreRepository.save(playedGenre);
    }
}
