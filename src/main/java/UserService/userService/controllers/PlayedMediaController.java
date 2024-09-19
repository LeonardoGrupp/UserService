package UserService.userService.controllers;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.services.PlayedMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playedMedia")
public class PlayedMediaController {

    private PlayedMediaService playedMediaService;

    @Autowired
    public PlayedMediaController(PlayedMediaService playedMediaService) {
        this.playedMediaService = playedMediaService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlayedMedia>> allPlayedMedia() {
        return ResponseEntity.ok(playedMediaService.allPlayedMedia());
    }

    @PostMapping("/create")
    public ResponseEntity<PlayedMedia> create(@RequestBody PlayedMedia playedMedia) {
        return ResponseEntity.ok(playedMediaService.create(playedMedia));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        return ResponseEntity.ok(playedMediaService.delete(id));
    }
}
