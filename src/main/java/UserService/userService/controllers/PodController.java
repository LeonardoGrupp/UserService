package UserService.userService.controllers;

import UserService.userService.services.PodService;
import UserService.userService.vo.Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/pod")
public class PodController {

    private PodService podService;

    @Autowired
    public PodController(PodService podService) {
        this.podService = podService;
    }

    @GetMapping("/all")
    ResponseEntity<List<Pod>> allPods() {
        return ResponseEntity.ok(podService.findAllPods());
    }
}
