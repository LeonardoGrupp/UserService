package UserService.userService.controllers;

import UserService.userService.services.VideoService;
import UserService.userService.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/video")
public class VideoController {

    private VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Video>> allVideos() {
        return ResponseEntity.ok(videoService.findAllVideos());
    }

//    @GetMapping("/recommendations/{id}")
//    public ResponseEntity<List<Video>> videoRecommendations(@PathVariable("id") long id) {
//        return ResponseEntity.ok(userService.videoRecommendations(id));
//    }

}


