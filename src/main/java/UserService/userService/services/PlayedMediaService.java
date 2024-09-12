package UserService.userService.services;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.repositories.PlayedMediaRepository;
import UserService.userService.vo.Album;
import UserService.userService.vo.Artist;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Media;
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

    public PlayedMedia save(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }
}
