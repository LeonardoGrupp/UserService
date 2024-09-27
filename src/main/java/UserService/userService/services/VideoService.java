package UserService.userService.services;

import UserService.userService.repositories.VideoRepository;
import UserService.userService.vo.Music;
import UserService.userService.vo.Pod;
import UserService.userService.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    private VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> findAllVideos() {
        return videoRepository.findAll();
    }

    public boolean videoExistsByUrl(String url) {
        return videoRepository.existsByUrlIgnoreCase(url);
    }

    public Video findVideoByUrl(String url) {
        Optional<Video> optionalVideo = videoRepository.findByUrlIgnoreCase(url);

        return optionalVideo.orElse(null);
    }

    public Video addPlay(Video video) {
        video.countPlay();
        videoRepository.save(video);

        return video;
    }
}
