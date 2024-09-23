package UserService.userService.controllers;

import UserService.userService.services.MusicService;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/music")
public class MusicController {

    private MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Music>> allMusic() {
        return ResponseEntity.ok(musicService.findAllMusic());
    }
}
