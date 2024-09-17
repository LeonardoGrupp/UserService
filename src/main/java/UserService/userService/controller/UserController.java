package UserService.userService.controller;

import UserService.userService.exception.UserNotFoundException;
import UserService.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/like/{mediaId}")
    public ResponseEntity<Void> likeMedia(@PathVariable long userId, @PathVariable long mediaId) {
        try {
            userService.likeMedia(userId, mediaId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{userId}/dislike/{mediaId}")
    public ResponseEntity<Void> dislikeMedia(@PathVariable long userId, @PathVariable long mediaId) {
        try {
            userService.dislikeMedia(userId, mediaId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{userId}/likedMedia")
    public ResponseEntity<List<Long>> getLikedMedia(@PathVariable long userId) {
        try {
            List<Long> likedMedia = userService.getLikedMedia(userId);
            return ResponseEntity.ok(likedMedia);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
