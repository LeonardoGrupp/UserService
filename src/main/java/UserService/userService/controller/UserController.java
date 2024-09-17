package UserService.userService.controller;

import UserService.userService.exception.UserNotFoundException;
import UserService.userService.request.MediaResponse;
import UserService.userService.service.MusicService;
import UserService.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {


}