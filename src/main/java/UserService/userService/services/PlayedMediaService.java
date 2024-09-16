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

    public PlayedMedia createMusicFromUser(Music music) {
        System.out.println("creating played media: music");
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate());

        System.out.println("saving PlayedMedia");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning PlayedMedia");
        return playedMedia;
    }

    public PlayedMedia save(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }
}
