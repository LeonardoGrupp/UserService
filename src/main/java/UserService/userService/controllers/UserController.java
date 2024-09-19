package UserService.userService.controllers;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.services.UserService;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> update(@PathVariable("id") long id, @RequestBody User newUserInfo) {
        return ResponseEntity.ok(userService.updateUser(id, newUserInfo));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.delete(id));
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<PlayedMedia> playMedia(@PathVariable("id") long id, @RequestParam("url") String url) {
        return ResponseEntity.ok(userService.playMedia(id, url));
    }

    @GetMapping("/songtest/{url}")
    public ResponseEntity<Music> testingMusic(@PathVariable("url") String url) {
        return ResponseEntity.ok(userService.testingMusic(url));
    }
}
