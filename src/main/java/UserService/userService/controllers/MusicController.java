package UserService.userService.controllers;

import UserService.userService.services.MusicService;
import UserService.userService.services.UserService;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/music")
public class MusicController {

    private MusicService musicService;
    private UserService userService;

    @Autowired
    public MusicController(MusicService musicService, UserService userService) {
        this.musicService = musicService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Music>> allMusic() {
        return ResponseEntity.ok(musicService.findAllMusic());
    }

    @GetMapping("/recommendations/{id}")
    public ResponseEntity<List<Music>> musicRecommendations(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.musicRecommendations(id));
    }
}
