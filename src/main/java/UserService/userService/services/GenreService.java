package UserService.userService.services;

import UserService.userService.repositories.GenreRepository;
import UserService.userService.vo.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreService {

    private GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre findGenreByGenre(String genreName) {
        return genreRepository.findGenreByGenreIgnoreCase(genreName);
    }

    public Genre findGenreByGenreTypeMusic(String genreName) {
        List<Genre> musicGenres = new ArrayList<>();

        for (Genre genre : getAllGenres()) {
            if (genre.getType().equalsIgnoreCase("music")) {
                musicGenres.add(genre);
            }
        }

        for (Genre genre : musicGenres) {
            if (genre.getGenre().equalsIgnoreCase(genreName)) {
                return genre;
            }
        }

        System.out.println("genre was not found in music with name: " + genreName);
        return null;
    }

    public Genre findGenreByGenreTypePod(String genreName) {
        List<Genre> podGenres = new ArrayList<>();

        for (Genre genre : getAllGenres()) {
            if (genre.getType().equalsIgnoreCase("pod")) {
                podGenres.add(genre);
            }
        }

        for (Genre genre : podGenres) {
            if (genre.getGenre().equalsIgnoreCase(genreName)) {
                return genre;
            }
        }

        System.out.println("genre was not found in pod with name: " + genreName);
        return null;
    }

    public Genre findGenreByGenreTypeVideo(String genreName) {
        List<Genre> videoGenres = new ArrayList<>();

        for (Genre genre : getAllGenres()) {
            if (genre.getType().equalsIgnoreCase("video")) {
                videoGenres.add(genre);
            }
        }

        for (Genre genre : videoGenres) {
            if (genre.getGenre().equalsIgnoreCase(genreName)) {
                return genre;
            }
        }

        System.out.println("genre was not found in video with name: " + genreName);
        return null;
    }

    public Genre addPlay(Genre genre) {
        genre.addPlay();
        genreRepository.save(genre);

        return genre;
    }
}
