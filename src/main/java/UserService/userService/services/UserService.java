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

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private PlayedMediaService playedMediaService;
    private PlayedGenreService playedGenreService;
    private MusicService musicService;
    private PodService podService;
    private VideoService videoService;

    @Autowired
    public UserService(UserRepository userRepository, PlayedMediaService playedMediaService,
                       PlayedGenreService playedGenreService, MusicService musicService,
                       VideoService videoService, PodService podService) {
        this.userRepository = userRepository;
        this.playedMediaService = playedMediaService;
        this.playedGenreService = playedGenreService;
        this.musicService = musicService;
        this.podService = podService;
        this.videoService = videoService;
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
            System.out.println("its music");

            // If person has NOT listened to the song before - create it
            if (!hasPlayedMediaBefore(user, url)) {
                System.out.println("music has not been played");

                // Get Media
                Music mediaToPlay = getMusicByUrl(url);
                System.out.println("recieved music");

                System.out.println();
                System.out.println("Genres:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());
                }


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                System.out.println("Creating empty playedGenreList");
                List<PlayedGenre> playedGenreList = new ArrayList<>();

                // Check all genres in music media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromMusicGenres(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");
                        // add +1 in play
                        PlayedGenre playedGenre = playedGenreService.findPlayedGenreByName(genre.getGenre());
                        playedGenre.countPlay();
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);

                    }
                }

                PlayedMedia savedMedia;
                System.out.println("sending music to playedMediaService.createMusicFromUser()");
                // Creating played media
                if (playedGenreList.isEmpty()) {
                    System.out.println("UserService: PlayedGenreList was empty!!!");
                    savedMedia = playedMediaService.createMusicFromUser(mediaToPlay);
                } else {
                    System.out.println("UserService: playedGenreList was not empty - sending list to create");
                    savedMedia = playedMediaService.createMusicFromUserWithList(mediaToPlay, playedGenreList);
                }

                System.out.println("media came back and created");

                System.out.println("adding music to users played media list");
                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                System.out.println("returning saved media");

                return savedMedia;


            } else {
                System.out.println("song has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");
                }
                System.out.println("music has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }
        else if(isPod(url)) {
            System.out.println("its pod");

            // If person has NOT watched the video before - create it
            if (!hasPlayedMediaBefore(user, url)) {
                System.out.println("pod has not been played");

                // Get Media
                Pod mediaToPlay = getPodByUrl(url);
                System.out.println("recieved pod");

                System.out.println();
                System.out.println("Genres:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());
                }


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                System.out.println("Creating empty playedGenreList");
                List<PlayedGenre> playedGenreList = new ArrayList<>();

                // Check all genres in music media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromPodGenres(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");
                        // add +1 in play
                        PlayedGenre playedGenre = playedGenreService.findPlayedGenreByName(genre.getGenre());
                        playedGenre.countPlay();
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);

                    }
                }

                PlayedMedia savedMedia;
                System.out.println("sending pod to playedMediaService.createPodFromUser()");
                // Creating played media
                if (playedGenreList.isEmpty()) {
                    System.out.println("UserService: PlayedGenreList was empty!!!");
                    savedMedia = playedMediaService.createPodFromUser(mediaToPlay);
                } else {
                    System.out.println("UserService: playedGenreList was not empty - sending list to create");
                    savedMedia = playedMediaService.createPodFromUserWithList(mediaToPlay, playedGenreList);
                }

                System.out.println("media came back and created");

                System.out.println("adding pod to users played media list");
                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                System.out.println("returning saved media");

                return savedMedia;


            } else {
                System.out.println("video has been viewed");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");
                }
                System.out.println("pod has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }
        else if(isVideo(url)) {
            System.out.println("its video");

            // If person has NOT watched the video before - create it
            if (!hasPlayedMediaBefore(user, url)) {
                System.out.println("video has not been played");

                // Get Media
                Video mediaToPlay = getVideoByUrl(url);
                System.out.println("recieved video");

                System.out.println();
                System.out.println("Genres:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());
                }


                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                System.out.println("Creating empty playedGenreList");
                List<PlayedGenre> playedGenreList = new ArrayList<>();

                // Check all genres in music media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedVideoGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromVideoGenres(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");
                        // add +1 in play
                        PlayedGenre playedGenre = playedGenreService.findPlayedGenreByName(genre.getGenre());
                        playedGenre.countPlay();
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);

                    }
                }

                PlayedMedia savedMedia;
                System.out.println("sending video to playedMediaService.createVideoFromUser()");
                // Creating played media
                if (playedGenreList.isEmpty()) {
                    System.out.println("UserService: PlayedGenreList was empty!!!");
                    savedMedia = playedMediaService.createVideoFromUser(mediaToPlay);
                } else {
                    System.out.println("UserService: playedGenreList was not empty - sending list to create");
                    savedMedia = playedMediaService.createVideoFromUserWithList(mediaToPlay, playedGenreList);
                }

                System.out.println("media came back and created");

                System.out.println("adding video to users played media list");
                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                System.out.println("returning saved media");

                return savedMedia;


            } else {
                System.out.println("video has been viewed");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");
                }
                System.out.println("video has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

        System.out.println("url is neither");
        return null;
    }

    public Music testingMusic(String url) {
        Music music = getMusicByUrl(url);

        System.out.println("Testing to see all genres within music");
        System.out.println("");
        for (Genre genre : music.getGenres()) {
            System.out.println("Genre " + genre.getGenre());
        }

        return music;
    }

    public boolean isMusic(String url) {

        boolean exists = musicService.musicExistsByUrl(url);

        if (exists) {
            System.out.println("url is music");
            return true;
        } else {
            System.out.println("url was not music");
            return false;
        }
    }

    public boolean isPod(String url) {

        boolean exists = podService.podExistsByUrl(url);

        if (exists) {
            System.out.println("url is pod");
            return true;
        } else {
            System.out.println("url was not pod");
            return false;
        }
    }

    public boolean isVideo(String url) {

        boolean exists = videoService.videoExistsByUrl(url);

        if (exists) {
            System.out.println("url is video");
            return true;
        } else {
            System.out.println("url was not video");
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

    public boolean hasPlayedGenreBefore(User user, Genre genre) {
        List<PlayedGenre> playedGenreListForUser = getUsersPlayedGenreList(user);


        for (PlayedGenre playedGenre : playedGenreListForUser) {
            if (playedGenre.getGenre().equals(genre.getGenre())) {
                return true;
            }
        }


        return false;
    }

    public boolean hasPlayedVideoGenreBefore(User user, Genre genre) {
        List<PlayedGenre> playedGenreListForUser = getUsersPlayedGenreList(user);


        for (PlayedGenre playedGenre : playedGenreListForUser) {
            if (playedGenre.getGenre().equals(genre.getGenre())) {
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

    public Music getMusicByUrl(String url) {
        Music musicToPlay = musicService.findMusicByUrl(url);

        if (musicToPlay == null) {
            System.out.println("ERROR - Music is null");
            return null;
        }

        return musicToPlay;
    }

    public Pod getPodByUrl(String url) {
        Pod podToPlay = podService.findPodByUrl(url);

        if (podToPlay == null) {
            System.out.println("ERROR - Pod is null");
            return null;
        }

        return podToPlay;
    }

    public Video getVideoByUrl(String url) {
        Video videoToPlay = videoService.findVideoByUrl(url);

        if (videoToPlay == null) {
            System.out.println("ERROR - Video is null");
            return null;
        }

        return videoToPlay;
    }
}
