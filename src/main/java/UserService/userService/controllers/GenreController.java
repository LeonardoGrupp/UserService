package UserService.userService.controllers;

import UserService.userService.services.GenreService;
import UserService.userService.vo.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/genre")
public class GenreController {

    private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Genre>> allGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Genre> allGenresByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(genreService.findGenreByGenre(name));
    }
}
