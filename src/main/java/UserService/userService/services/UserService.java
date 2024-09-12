package UserService.userService.services;

import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.repositories.PlayedMediaRepository;
import UserService.userService.repositories.UserRepository;
import UserService.userService.vo.Album;
import UserService.userService.vo.Artist;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Media;
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

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate, PlayedMediaService playedMediaService) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.playedMediaService = playedMediaService;
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

        System.out.println("user found - checking to see if song is already played");

        // If person has NOT listened to the song before - create it
        if (!hasListenedBefore(user, url)) {
            System.out.println("song has not been played before");

            System.out.println("fetching media by url");
            // Get Media
            Media mediaToPlay = getMediaByUrl(url);

            System.out.println("recieved the mediaToPlay with the resttemplate media inside");


            if (mediaToPlay == null) {
                System.out.println("mediaToPlay is null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
            }

            System.out.println("Sending Media to create Played Media");
            PlayedMedia savedMedia = playedMediaService.createFromUser(mediaToPlay);

            user.addMediaToPlayedMedia(savedMedia);

            System.out.println("EVEYRTHING SHOULD BE RETURNING NOW");

            return savedMedia;


        } else {
            System.out.println("song has been played");
            // Else if person HAS listened to song - get it from persons list, add play+1 and return
            PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

            mediaBeenPlayed.countPlay();
            System.out.println("media has been counted play");

            return playedMediaService.save(mediaBeenPlayed);
        }

    }

    public boolean hasListenedBefore(User user, String url) {
        List<PlayedMedia> playedMediaListForUser = getUsersPlayedMediaList(user);

        for (PlayedMedia playedMedia : playedMediaListForUser) {
            if (playedMedia.getUrl().equals(url)) {
                System.out.println("return true - song has been played");
                return true;
            }
        }

        System.out.println("returning false - song has not been played");
        return false;
    }

    public List<PlayedMedia> getUsersPlayedMediaList(User user) {
        return user.getPlayedMedia();
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
        System.out.println("fetching in getMedia method with resttemplate");
        ResponseEntity<Media> fetchedMedia = restTemplate.getForEntity("lb://media-service/media/get/" + url, Media.class);

        System.out.println("response entity fetched");

        Media mediaToPlay = fetchedMedia.getBody();
        System.out.println("fetchedMedia.getBody put into mediaToPlay");

        System.out.println("returning mediaToPlay");
        return mediaToPlay;
    }
}
