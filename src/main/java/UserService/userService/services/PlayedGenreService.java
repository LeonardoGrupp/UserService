package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.repositories.PlayedGenreRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
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

    public PlayedGenre create(PlayedGenre playedGenre) {
        return playedGenreRepository.save(playedGenre);
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
