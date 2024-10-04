package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.repositories.PlayedGenreRepository;
import UserService.userService.vo.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public PlayedGenre findPlayedGenreById(long id) {
        Optional<PlayedGenre> optionalPlayedGenre = playedGenreRepository.findById(id);

        if (optionalPlayedGenre.isPresent()) {
            return optionalPlayedGenre.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: PlayedGenre ID not found");
        }
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: PlayedGenre could not be found with ID");
        }

        playedGenreRepository.delete(genreToDelete);

        return "PlayedGenre deleted";
    }
}
