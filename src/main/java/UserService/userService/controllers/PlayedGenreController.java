package UserService.userService.controllers;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.services.PlayedGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playedGenre")
public class PlayedGenreController {

    private PlayedGenreService playedGenreService;

    @Autowired
    public PlayedGenreController(PlayedGenreService playedGenreService) {
        this.playedGenreService = playedGenreService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlayedGenre>> allPlayedGenres() {
        return ResponseEntity.ok(playedGenreService.allPlayedGenres());
    }

    @PostMapping("/create")
    public ResponseEntity<PlayedGenre> create(@RequestBody PlayedGenre playedGenre) {
        return ResponseEntity.ok(playedGenreService.create(playedGenre));
    }
}
