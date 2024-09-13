package UserService.userService.services;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.repositories.PlayedMediaRepository;
import UserService.userService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayedMediaService {

    private PlayedMediaRepository playedMediaRepository;

    @Autowired
    public PlayedMediaService(PlayedMediaRepository playedMediaRepository) {
        this.playedMediaRepository = playedMediaRepository;
    }

    public List<PlayedMedia> allPlayedMedia() {
        return playedMediaRepository.findAll();
    }

    public PlayedMedia create(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }

    public PlayedMedia createFromUser(Media media) {

        System.out.println("1 Creating played media");
        PlayedMedia playedMedia = new PlayedMedia(media.getType(), media.getTitle(), media.getUrl(), media.getReleaseDate(), media.getGenres());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createMusicFromUser(Music music) {
        System.out.println("creating played media: music");
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate(), music.getGenres());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createVideoFromUser(Video video) {
        System.out.println("creating played media: video");
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate(), video.getGenres());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createPodFromUser(Pod pod) {
        System.out.println("creating played media: pod");
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate(), pod.getGenres());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia save(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }
}
