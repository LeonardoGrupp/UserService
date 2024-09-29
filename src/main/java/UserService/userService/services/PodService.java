package UserService.userService.services;

import UserService.userService.repositories.PodRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import UserService.userService.vo.Pod;
import UserService.userService.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PodService {

    private PodRepository podRepository;

    @Autowired
    public PodService(PodRepository podRepository) {
        this.podRepository = podRepository;
    }

    public List<Pod> findAllPods() {
        return podRepository.findAll();
    }

    public boolean podExistsByUrl(String url) {
        return podRepository.existsByUrlIgnoreCase(url);
    }

    public Pod findPodByUrl(String url) {
        Optional<Pod> optionalPod = podRepository.findByUrlIgnoreCase(url);

        if (optionalPod.isPresent()) {
            return optionalPod.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
        }
    }

    public List<Pod> findAllPodsInGenre(Genre genre) {
        return podRepository.findPodsByGenres(genre);
    }

    public Pod addPlay(Pod pod) {
        pod.countPlay();
        podRepository.save(pod);

        return pod;
    }
}
