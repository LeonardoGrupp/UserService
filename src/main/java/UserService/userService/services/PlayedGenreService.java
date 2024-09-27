package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.repositories.PlayedGenreRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return playedGenreRepository.findPlayedGenreByGenreIgnoreCase(genre);
    }

    public PlayedGenre findPlayedVideoGenreByName(String genre) {
        List<PlayedGenre> allGenres = playedGenreRepository.findAll();

        List<PlayedGenre> allVideoGenres = new ArrayList<>();

        for (PlayedGenre playedGenre : allGenres) {
            if (playedGenre.getType().equalsIgnoreCase("video")) {
                allVideoGenres.add(playedGenre);
            }
        }

        for (PlayedGenre playedGenre : allVideoGenres) {
            if (playedGenre.getGenre().equalsIgnoreCase(genre)) {
                return playedGenre;
            }
        }

        System.out.println("NO VIDEO WITH THAT NAME WAS FOUND");
        return null;
    }

    public PlayedGenre findPlayedGenreById(long id) {
        Optional<PlayedGenre> optionalPlayedGenre = playedGenreRepository.findById(id);

        return optionalPlayedGenre.orElse(null);
    }

    public PlayedGenre create(PlayedGenre playedGenre) {
        return playedGenreRepository.save(playedGenre);
    }

    public PlayedGenre createFromMusicGenres(Genre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre(), "music"));
    }

    public PlayedGenre createFromPodGenres(Genre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre(), "pod"));
    }

    public PlayedGenre createFromVideoGenres(Genre genre) {
        return playedGenreRepository.save(new PlayedGenre(genre.getGenre(), "video"));
    }

    public PlayedGenre save(PlayedGenre playedGenre) {
        return playedGenreRepository.save(playedGenre);
    }

    public String delete(long id) {
        PlayedGenre genreToDelete = findPlayedGenreById(id);

        if (genreToDelete == null) {
            System.out.println("PlayedGenreService() - couldnt find PlayedGenre with id: " + id);
        }

        playedGenreRepository.delete(genreToDelete);

        return "PlayedGenre deleted";
    }
}
