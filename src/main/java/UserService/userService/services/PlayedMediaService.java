package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.repositories.PlayedMediaRepository;
import UserService.userService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createMusicFromUserWithList(Music music, List<PlayedGenre> playedGenreList) {
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createPodFromUser(Pod pod) {
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createPodFromUserWithList(Pod pod, List<PlayedGenre> playedGenreList) {
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createVideoFromUser(Video video) {
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate());

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia createVideoFromUserWithList(Video video, List<PlayedGenre> playedGenreList) {
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        playedMediaRepository.save(playedMedia);

        return playedMedia;
    }

    public PlayedMedia save(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }

    public String delete(long id) {
        Optional<PlayedMedia> optionalPlayedMediaToDelete = playedMediaRepository.findById(id);

        if (optionalPlayedMediaToDelete.isPresent()) {
            playedMediaRepository.delete(optionalPlayedMediaToDelete.get());

            return "Deleted PlayedMedia item";
        } else {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: PlayedMedia was not found");
        }
    }
}
