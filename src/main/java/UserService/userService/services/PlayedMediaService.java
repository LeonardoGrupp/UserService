package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.repositories.PlayedMediaRepository;
import UserService.userService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayedMediaService {

    private PlayedMediaRepository playedMediaRepository;
    private PlayedGenreService playedGenreService;

    @Autowired
    public PlayedMediaService(PlayedMediaRepository playedMediaRepository, PlayedGenreService playedGenreService) {
        this.playedMediaRepository = playedMediaRepository;
        this.playedGenreService = playedGenreService;
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

    public PlayedMedia createMusicFromUserWithList(Music music, List<PlayedGenre> playedGenreList) {
        System.out.println("Creating played music with genre list");
        PlayedMedia playedMedia = new PlayedMedia(music.getType(), music.getTitle(), music.getUrl(), music.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        System.out.println("saving");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning played media with list");
        return playedMedia;
    }

    public PlayedMedia createPodFromUser(Pod pod) {
        System.out.println("creating played media: pod");
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate());

        System.out.println("saving PlayedMedia");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning PlayedMedia");
        return playedMedia;
    }

    public PlayedMedia createPodFromUserWithList(Pod pod, List<PlayedGenre> playedGenreList) {
        System.out.println("Creating played pod with genre list");
        PlayedMedia playedMedia = new PlayedMedia(pod.getType(), pod.getTitle(), pod.getUrl(), pod.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        System.out.println("saving");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning played media with list");
        return playedMedia;
    }

    public PlayedMedia createVideoFromUser(Video video) {
        System.out.println("creating played media: video");
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate());

        System.out.println("saving PlayedMedia");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning PlayedMedia");
        return playedMedia;
    }

    public PlayedMedia createVideoFromUserWithList(Video video, List<PlayedGenre> playedGenreList) {
        System.out.println("Creating played video with genre list");
        PlayedMedia playedMedia = new PlayedMedia(video.getType(), video.getTitle(), video.getUrl(), video.getReleaseDate());
        playedMedia.setGenres(playedGenreList);

        System.out.println("saving");
        playedMediaRepository.save(playedMedia);

        System.out.println("returning played media with list");
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

            return "Could not delete the PlayedMedia item due to it being null";
        }
    }
}
