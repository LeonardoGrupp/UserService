package UserService.userService.services;

import UserService.userService.repositories.PodRepository;
import UserService.userService.vo.Pod;
import UserService.userService.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return optionalPod.orElse(null);
    }
}
