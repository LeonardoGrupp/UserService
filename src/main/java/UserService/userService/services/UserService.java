package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.repositories.UserRepository;
import UserService.userService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private RestTemplate restTemplate;
    private PlayedMediaService playedMediaService;
    private PlayedGenreService playedGenreService;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate, PlayedMediaService playedMediaService, PlayedGenreService playedGenreService) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.playedMediaService = playedMediaService;
        this.playedGenreService = playedGenreService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User create(User user) {
        if (user.getUsername().isEmpty() || user.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ERROR: Username not provided");
        }

        return userRepository.save(user);
    }

    public User updateUser(long id, User newInfo) {
        User existingUser = findUserById(id);

        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        if (!newInfo.getUsername().isEmpty() && newInfo.getUsername() != null && !newInfo.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(newInfo.getUsername());
        }

        return userRepository.save(existingUser);
    }

    public String delete(long id) {
        User userToDelete = findUserById(id);

        if (userToDelete == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        userRepository.delete(userToDelete);

        return "User successfully deleted";
    }

    public PlayedMedia playMedia(long id, String url) {
        // Get User
        User user = findUserById(id);
        System.out.println("finding user");



        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        // get music by url, get video by url, get pod by url -  skicka med type till hasPlayedMediaBefore
        if (isMusic(url)) {

            // If person has NOT listened to the song before - create it
            if (!hasPlayedMediaBefore(user, url)) {

                // Get Media
                Music mediaToPlay = getMusicByUrl(url);


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }
                // Creating played media
                PlayedMedia savedMedia = playedMediaService.createMusicFromUser(mediaToPlay);

                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;


            } else {
                System.out.println("song has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                System.out.println("music has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

        if (isVideo(url)) {
            // If person has NOT listened to the song before - create it
            if (!hasPlayedMediaBefore(user, url)) {

                // Get Media
                Video mediaToPlay = getVideoByUrl(url);


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }
                // Creating played media
                PlayedMedia savedMedia = playedMediaService.createVideoFromUser(mediaToPlay);

                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;


            } else {
                System.out.println("video has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                System.out.println("video has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

        if (isPod(url)) {
            if (!hasPlayedMediaBefore(user, url)) {

                // Get Media
                Pod mediaToPlay = getPodByUrl(url);


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }
                // Creating played media
                PlayedMedia savedMedia = playedMediaService.createPodFromUser(mediaToPlay);

                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;


            } else {
                System.out.println("pod has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                System.out.println("pod has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

        System.out.println("url is neither");
        return null;
    }

    public Boolean isMusic(String url) {
        ResponseEntity<Boolean> musicExistsInfo = restTemplate.getForEntity("lb://music-service/music/exists/" + url, Boolean.class);

        Boolean exists = musicExistsInfo.getBody();

        if (exists) {
            System.out.println("url is music");
            return true;
        } else {
            System.out.println("url was not music");
            return false;
        }
    }

    public Boolean isVideo(String url) {
        ResponseEntity<Boolean> videoExistsInfo = restTemplate.getForEntity("lb://video-service/video/exists/" + url, Boolean.class);

        Boolean exists = videoExistsInfo.getBody();

        if (exists) {
            System.out.println("url is video");
            return true;
        } else {
            System.out.println("url was not video");
            return false;
        }
    }

    public Boolean isPod(String url) {
        ResponseEntity<Boolean> podExistsInfo = restTemplate.getForEntity("lb://pod-service/pod/exists/" + url, Boolean.class);

        Boolean exists = podExistsInfo.getBody();

        if (exists) {
            System.out.println("url is pod");
            return true;
        } else {
            System.out.println("url was not pod");
            return false;
        }
    }



    public boolean hasPlayedMediaBefore(User user, String url) {
        List<PlayedMedia> playedMediaListForUser = getUsersPlayedMediaList(user);

        for (PlayedMedia playedMedia : playedMediaListForUser) {
            if (playedMedia.getUrl().equals(url)) {
                return true;
            }
        }

        return false;
    }

    public List<PlayedMedia> getUsersPlayedMediaList(User user) {
        return user.getPlayedMedia();
    }

    public List<PlayedGenre> getUsersPlayedGenreList(User user) {
        return user.getPlayedGenre();
    }

    public PlayedMedia getMediaFromUsersMediaList(User user, String url) {
        List<PlayedMedia> usersList = getUsersPlayedMediaList(user);

        for (PlayedMedia playedMedia : usersList) {
            if (playedMedia.getUrl().equals(url)) {
                return playedMedia;
            }
        }

        return null;
    }

    public Media getMediaByUrl(String url) {
        ResponseEntity<Media> fetchedMedia = restTemplate.getForEntity("lb://media-service/media/get/" + url, Media.class);

        Media mediaToPlay = fetchedMedia.getBody();

        return mediaToPlay;
    }

    public Music getMusicByUrl(String url) {
        ResponseEntity<Music> fetchedMusic = restTemplate.getForEntity("lb://music-service/music/get/" + url, Music.class);

        Music mediaToPlay = fetchedMusic.getBody();

        return mediaToPlay;
    }

    public Video getVideoByUrl(String url) {
        ResponseEntity<Video> fetchedVideo = restTemplate.getForEntity("lb://video-service/video/get/" + url, Video.class);

        Video mediaToPlay = fetchedVideo.getBody();

        return mediaToPlay;
    }

    public Pod getPodByUrl(String url) {
        ResponseEntity<Pod> fetchedMedia = restTemplate.getForEntity("lb://pod-service/pod/get/" + url, Pod.class);

        Pod mediaToPlay = fetchedMedia.getBody();

        return mediaToPlay;
    }
}
