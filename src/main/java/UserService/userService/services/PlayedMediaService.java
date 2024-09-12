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
        PlayedMedia playedMedia = new PlayedMedia(media.getType(), media.getTitle(), media.getUrl(), media.getReleaseDate());
        System.out.println("1 SUCCESS - Played media created");
        System.out.println("2 adding media");
//        playedMedia.setMedia(media);
        System.out.println("2 media added");
        System.out.println();
        System.out.println("2 SUCCESS - Genres, Albums and Artists addded");
        System.out.println();
        System.out.println("3 Checking genre:");
//        for (Genre genre : playedMedia.getMedia().getGenres()) {
//            System.out.println(genre.getGenre());
//        }
//        System.out.println("3 Checking Albums:");
//        for (Album album : playedMedia.getMedia().getAlbums()) {
//            System.out.println(album.getName());
//        }
//        System.out.println("3 Checking Artists:");
//        for (Artist artist : playedMedia.getMedia().getArtists()) {
//            System.out.println(artist.getName());
//        }
        System.out.println();
        System.out.println("3. SUCCESS - existing genres albums and artist");

        System.out.println("4. saving played media");
        playedMediaRepository.save(playedMedia);
        System.out.println("4. SUCCESS SAVED");

        System.out.println("5. returning played media");
        return playedMedia;
    }

    public PlayedMedia save(PlayedMedia playedMedia) {
        return playedMediaRepository.save(playedMedia);
    }
}
