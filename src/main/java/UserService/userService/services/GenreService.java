package UserService.userService.services;

import UserService.userService.repositories.GenreRepository;
import UserService.userService.vo.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
