package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.repositories.UserRepository;
import UserService.userService.vo.*;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private PlayedMediaService playedMediaService;
    private PlayedGenreService playedGenreService;
    private MusicService musicService;
    private PodService podService;
    private VideoService videoService;
    private GenreService genreService;

    @Autowired
    public UserService(UserRepository userRepository, PlayedMediaService playedMediaService,
                       PlayedGenreService playedGenreService, MusicService musicService,
                       VideoService videoService, PodService podService, GenreService genreService) {
        this.userRepository = userRepository;
        this.playedMediaService = playedMediaService;
        this.playedGenreService = playedGenreService;
        this.musicService = musicService;
        this.podService = podService;
        this.videoService = videoService;
        this.genreService = genreService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
        }
    }

    public User findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);

        if (user != null) {
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
        }
    }

    public User create(User user) {
        if (user.getUsername().isEmpty() || user.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ERROR: Username not provided");
        }

        return userRepository.save(user);
    }

    public User updateUser(long id, User newInfo) {
        User existingUser = findUserById(id);

        if (!newInfo.getUsername().isEmpty() && newInfo.getUsername() != null && !newInfo.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(newInfo.getUsername());
        }

        return userRepository.save(existingUser);
    }

    public String delete(long id) {
        User userToDelete = findUserById(id);

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

                System.out.println("Creating empty playedGenreList");
                List<PlayedGenre> playedGenreList = new ArrayList<>();

                // Check all genres in music media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedMusicGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromMusicGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");

                        // add +1 in play
                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenreOfUser.getType().equalsIgnoreCase("music")) {
                                playedGenre = playedGenreOfUser;
                            }
                        }

                        if (playedGenre == null) {
                            System.out.println("ERROR PLAYED GENRE IS NULL");
                            return null;
                        }

                        genreService.addPlay(genre);

                        System.out.println("got genre");
                        playedGenre.countPlay();
                        System.out.println("counted play");
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        System.out.println("saved genre");

                        playedGenreList.add(savedGenre);

                        System.out.println("added savedGenre to person");

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

                System.out.println("adding play to Music-object");
                musicService.addPlay(mediaToPlay);

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

                System.out.println("adding play to Music-object");
                Music mediaToPlay = getMusicByUrl(url);
                musicService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");

                    // TODO MAKE IT GET MUSIC TYPE
                    Genre genreToPlay = genreService.findGenreByGenreTypeMusic(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                    System.out.println("the genre has been counted play");
                }
                System.out.println("music has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        } else if (isPod(url)) {
            System.out.println("its pod");

            // If person has NOT listened to the song before - create it
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

                // Check all genres in pod media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedPodGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromPodGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");

                        // add +1 in play
                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenreOfUser.getType().equalsIgnoreCase("pod")) {
                                playedGenre = playedGenreOfUser;
                            }
                        }

                        if (playedGenre == null) {
                            System.out.println("ERROR PLAYED GENRE IS NULL");
                            return null;
                        }

                        genreService.addPlay(genre);

                        System.out.println("got genre");
                        playedGenre.countPlay();
                        System.out.println("counted play");
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        System.out.println("saved genre");

                        playedGenreList.add(savedGenre);

                        System.out.println("added savedGenre to person");

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

                System.out.println("adding play to Pod-object");
                podService.addPlay(mediaToPlay);

                System.out.println("media came back and created");

                System.out.println("adding pod to users played media list");
                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                System.out.println("returning saved media");

                return savedMedia;


            } else {
                System.out.println("pod has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                System.out.println("adding play to Pod-object");
                Pod mediaToPlay = getPodByUrl(url);
                podService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");

                    Genre genreToPlay = genreService.findGenreByGenreTypePod(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                    System.out.println("the genre has been counted play");
                }
                System.out.println("pod has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        } else if (isVideo(url)) {
            System.out.println("its video");

            // If person has NOT listened to the song before - create it
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

                // Check all genres in pod media
                System.out.println("Checking genre:");
                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    // if the genre has not been played - create it
                    if (!hasPlayedVideoGenreBefore(user, genre)) {
                        System.out.println("genre was not played before - creating");
                        PlayedGenre playedGenre = playedGenreService.createFromVideoGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");

                        // add +1 in play
                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenreOfUser.getType().equalsIgnoreCase("video")) {
                                playedGenre = playedGenreOfUser;
                            }
                        }

                        if (playedGenre == null) {
                            System.out.println("ERROR PLAYED GENRE IS NULL");
                            return null;
                        }

                        genreService.addPlay(genre);

                        System.out.println("got genre");
                        playedGenre.countPlay();
                        System.out.println("counted play");
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        System.out.println("saved genre");

                        playedGenreList.add(savedGenre);

                        System.out.println("added savedGenre to person");

                    }

                }

                PlayedMedia savedMedia;
                System.out.println("sending video to playedMediaService.createPodFromUser()");
                // Creating played media
                if (playedGenreList.isEmpty()) {
                    System.out.println("UserService: PlayedGenreList was empty!!!");
                    savedMedia = playedMediaService.createVideoFromUser(mediaToPlay);
                } else {
                    System.out.println("UserService: playedGenreList was not empty - sending list to create");
                    savedMedia = playedMediaService.createVideoFromUserWithList(mediaToPlay, playedGenreList);
                }

                System.out.println("adding play to Pod-object");
                videoService.addPlay(mediaToPlay);

                System.out.println("media came back and created");

                System.out.println("adding video to users played media list");
                // Saving played media to user
                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                System.out.println("returning saved media");

                return savedMedia;


            } else {
                System.out.println("video has been played");
                // Else if person HAS listened to song - get it from persons list, add play+1 and return
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                System.out.println("adding play to Video-object");
                Video mediaToPlay = getVideoByUrl(url);
                videoService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);
                    System.out.println("counted play for genre");

                    Genre genreToPlay = genreService.findGenreByGenreTypeVideo(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                    System.out.println("the genre has been counted play");
                }
                System.out.println("video has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

        System.out.println("url is neither");
        return null;
    }

    public PlayedMedia likeMedia(long id, String url) {
        User user = findUserById(id);

        if (!hasPlayedMediaBefore(user, url)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERROR: In order to like media you first need to play it");

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equals(url)) {
                    if (playedMedia.isLiked() && user.getLikedMedia().contains(playedMedia)) {
                        throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: media already liked");

                    } else {

                        playedMedia.likeMedia();

                        playedMediaService.save(playedMedia);

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);

                        userRepository.save(user);

                        return playedMedia;
                    }

                }
            }

            // Will never reach this due to boolean
            return null;
        }
    }

    public PlayedGenre likeGenre(long id, String genreName) {
        User user = findUserById(id);

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName) && !playedGenre.isLiked()) {

                playedGenre.likeGenre();

                playedGenreService.save(playedGenre);

                user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);

                userRepository.save(user);

                return playedGenre;
            } else if (playedGenre.getGenre().equalsIgnoreCase(genreName) && playedGenre.isLiked()) {
                throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: Already liked genre");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
    }

    public PlayedMedia disLikeMedia(long id, String url) {
        // Get User
        User user = findUserById(id);

        if (!hasPlayedMediaBefore(user, url)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERROR: In order to dislike media you first need to play it");

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equals(url)) {
                    if (playedMedia.isDisliked() && user.getDisLikedMedia().contains(playedMedia)) {
                        throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: media already disliked");
                    } else {
                        playedMedia.disLikeMedia();

                        playedMediaService.save(playedMedia);

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);

                        userRepository.save(user);

                        return playedMedia;
                    }
                }
            }

            // will never reach this due to boolean
            return null;
        }
    }

    public PlayedGenre disLikeGenre(long id, String genreName) {
        User user = findUserById(id);

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName) && !playedGenre.isDisliked()) {

                playedGenre.disLikeGenre();

                playedGenreService.save(playedGenre);

                user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);

                userRepository.save(user);

                return playedGenre;

            } else if (playedGenre.getGenre().equalsIgnoreCase(genreName) && playedGenre.isDisliked()) {
                throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: Already disliked genre");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
    }

    public PlayedMedia resetLikesAndDisLikesOfMedia(long id, String url) {
        // Get User
        User user = findUserById(id);

        if (!hasPlayedMediaBefore(user, url)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERROR: In order to reset likes/dislikes media you first need to play it");

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equalsIgnoreCase(url)) {
                    if (!playedMedia.isLiked() && !playedMedia.isDisliked()) {
                        throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: Media is already false on both like and dislike");

                    } else {
                        playedMedia.resetLikeAndDisLikeMedia();

                        playedMediaService.save(playedMedia);

                        System.out.println("media likes and dislikes has been reset");

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
                        userRepository.save(user);

                        return playedMedia;
                    }
                }
            }

            // will never reach this due to boolean
            return null;
        }
    }

    public PlayedGenre resetLikesAndDisLikesOfGenre(long id, String genreName) {
        User user = findUserById(id);

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName)) {
                if (!playedGenre.isLiked() && !playedGenre.isDisliked()) {
                    throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "ERROR: Genre is already false on both like and dislike");

                } else {
                    playedGenre.resetLikeAndDisLikeGenre();

                    playedGenreService.save(playedGenre);

                    System.out.println("genre likes and dislikes has been reset");

                    user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
                    userRepository.save(user);

                    return playedGenre;
                }
            }
        }

        // will never reach this due to boolean
        return null;
    }

    public Music findMusicByUrl(String url) {
        Music music = getMusicByUrl(url);

        return music;
    }

    public boolean isMusic(String url) {

        boolean exists = musicService.musicExistsByUrl(url);

        if (exists) {
            return true;
        } else {
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

    public boolean hasPlayedMusicGenreBefore(User user, Genre genre) {
        List<PlayedGenre> playedGenreListForUser = getUsersPlayedGenreList(user);


        for (PlayedGenre playedGenre : playedGenreListForUser) {
            if (playedGenre.getGenre().equals(genre.getGenre()) && playedGenre.getType().equalsIgnoreCase("music")) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPlayedPodGenreBefore(User user, Genre genre) {
        List<PlayedGenre> playedGenreListOfUser = getUsersPlayedGenreList(user);

        for (PlayedGenre playedGenre : playedGenreListOfUser) {
            if (playedGenre.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenre.getType().equalsIgnoreCase("pod")) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPlayedVideoGenreBefore(User user, Genre genre) {
        List<PlayedGenre> playedGenreListForUser = getUsersPlayedGenreList(user);


        for (PlayedGenre playedGenre : playedGenreListForUser) {
            if (playedGenre.getGenre().equals(genre.getGenre()) && playedGenre.getType().equalsIgnoreCase("video")) {
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

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: not found");
    }

    public Music getMusicByUrl(String url) {
        Music musicToPlay = musicService.findMusicByUrl(url);

        return musicToPlay;
    }

    public Pod getPodByUrl(String url) {
        Pod podToPlay = podService.findPodByUrl(url);

        return podToPlay;
    }

    public Video getVideoByUrl(String url) {
        Video videoToPlay = videoService.findVideoByUrl(url);

        return videoToPlay;
    }

    public List<Video> videoRecommendations(long id) {
        User user = findUserById(id);

        return totalTop10Videos(user);
    }

    public List<Pod> podRecommendations(long id) {
        User user = findUserById(id);

        return totalTop10Pods(user);
    }

    public List<Music> musicRecommendations(long id) {
        User user = findUserById(id);

        return totalTop10Songs(user);
    }

    public List<PlayedGenre> getUsersMostPlayedGenresSortedByPlays(User user, String type) {
        List<PlayedGenre> usersGenres = user.getPlayedGenre();

        List<PlayedGenre> sortedTopGenreList = new ArrayList<>();

        if (type.equalsIgnoreCase("music")) {

            // Get liked genres
            List<PlayedGenre> likedUserMusicGenres = new ArrayList<>();
            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isLiked()) {
                    likedUserMusicGenres.add(playedGenre);
                }
            }

            // if liked genres is empty
            List<PlayedGenre> userMusicGenres = new ArrayList<>();
            if (likedUserMusicGenres.isEmpty()) {

                for (PlayedGenre playedGenre : usersGenres) {
                    if (playedGenre.getType().equalsIgnoreCase("music") && !playedGenre.isDisliked()) {
                        userMusicGenres.add(playedGenre);
                    }
                }
            }

            List<PlayedGenre> sortedFullGenreList = new ArrayList<>();

            if (!likedUserMusicGenres.isEmpty()) {
                sortedFullGenreList = likedUserMusicGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());
            }
            if (likedUserMusicGenres.isEmpty()) {
                sortedFullGenreList = userMusicGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());
            }


            if (sortedFullGenreList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            } else if (sortedFullGenreList.size() < 3) {
                for (int i = 0; i < sortedFullGenreList.size(); i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            }
        }
        if (type.equalsIgnoreCase("pod")) {

            List<PlayedGenre> userPodGenres = new ArrayList<>();
            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("pod")) {
                    userPodGenres.add(playedGenre);
                }
            }

            List<PlayedGenre> sortedFullGenreList = userPodGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());


            if (sortedFullGenreList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            } else if (sortedFullGenreList.size() < 3) {
                for (int i = 0; i < sortedFullGenreList.size(); i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            }
        }
        if (type.equalsIgnoreCase("video")) {

            List<PlayedGenre> userVideoGenres = new ArrayList<>();
            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("video")) {
                    userVideoGenres.add(playedGenre);
                }
            }

            List<PlayedGenre> sortedFullGenreList = userVideoGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());


            if (sortedFullGenreList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            } else if (sortedFullGenreList.size() < 3) {
                for (int i = 0; i < sortedFullGenreList.size(); i++) {
                    sortedTopGenreList.add(sortedFullGenreList.get(i));
                }
            }
        }

        return sortedTopGenreList;
    }

    public List<PlayedGenre> sortAllPlayedGenresByPlays(User user, String type) {
        System.out.println("sortAllPlayedGenresByPlayed");
        List<PlayedGenre> usersGenres = user.getPlayedGenre();

        List<PlayedGenre> userGenresToSendBack = new ArrayList<>();

        if (type.equals("music")) {

            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isLiked()) {
                    userGenresToSendBack.add(playedGenre);
                }
            }

            // If there was no liked genres, add all genres of same type
            if (userGenresToSendBack.isEmpty()) {

                for (PlayedGenre playedGenre : usersGenres) {
                    if (playedGenre.getType().equals("music") && !playedGenre.isDisliked()) {
                        System.out.println("adding playedGenre: " + playedGenre.getGenre());
                        userGenresToSendBack.add(playedGenre);
                    }
                }
            }

            // Checking for disliked genres
            if (!userGenresToSendBack.isEmpty()) {
                System.out.println("checking if anything is disliked");
                for (PlayedGenre playedGenre : usersGenres) {
                    if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isDisliked()) {
                        System.out.println(playedGenre.getGenre() + " WAS DISLIKED ***** REMOVING");
                        userGenresToSendBack.remove(playedGenre);
                    }
                }
            }

        }

        if (type.equals("pod")) {

            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("pod")) {
                    userGenresToSendBack.add(playedGenre);
                }
            }
        }

        if (type.equals("video")) {

            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("video")) {
                    userGenresToSendBack.add(playedGenre);
                }
            }
        }

        List<PlayedGenre> sortedGenreList = userGenresToSendBack.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());

        return sortedGenreList;
    }

    public List<Genre> sortGenresByPlays(List<Genre> list) {
        List<Genre> sortedGenres = list.stream().sorted(Comparator.comparingInt(Genre::getTotalPlays).reversed()).collect(Collectors.toList());

        return sortedGenres;
    }

    public List<Music> sortAllMusicByPlays(List<Music> musicList) {
        List<Music> sortedMusicList = musicList.stream().sorted(Comparator.comparingInt(Music::getPlayCounter).reversed()).collect(Collectors.toList());

        return sortedMusicList;
    }

    public List<Video> sortAllVideosByPlays(List<Video> videoList) {
        List<Video> sortedVideoList = videoList.stream().sorted(Comparator.comparingInt(Video::getPlayCounter).reversed()).collect(Collectors.toList());

        return sortedVideoList;
    }

    public List<Pod> sortAllPodsByPlays(List<Pod> podList) {
        List<Pod> sortedPodList = podList.stream().sorted(Comparator.comparingInt(Pod::getPlayCounter).reversed()).collect(Collectors.toList());

        return sortedPodList;
    }

    public List<Genre> convertUserPlayedGenresToGenre(List<PlayedGenre> playedGenreList) {

        List<Genre> topGenres = new ArrayList<>();
        for (PlayedGenre playedGenre : playedGenreList) {
            topGenres.add(genreService.findGenreByGenre(playedGenre.getGenre()));
        }

        return topGenres;
    }

    public List<Video> totalTop10Videos(User user) {

        List<Video> allVideos = videoService.findAllVideos();

        List<Video> videosToDelete = new ArrayList<>();

        // lägg till alla videos user kollat på
        for (PlayedMedia playedMedia : user.getPlayedMedia()) {
            for (Video video : allVideos) {
                if (playedMedia.getUrl().equalsIgnoreCase(video.getUrl())) {
                    videosToDelete.add(video);
                }
            }
        }

        // deletar alla dubletter
        if (!videosToDelete.isEmpty()) {
            for (Video video : videosToDelete) {
                allVideos.remove(video);
            }
        }

        System.out.println("");
        System.out.println("those videos are now removed");
        System.out.println("size of allVideos: " + allVideos.size());


        List<Video> top10videos = new ArrayList<>();

        // if lista is not empty - return most listened pods that user has not listened to
        if (allVideos.size() >= 10) {
            List<Video> sortedAllVideos = sortAllVideosByPlays(allVideos);


            // extract top 10
            for (int i = 0; i < 10; i++) {
                top10videos.add(sortedAllVideos.get(i));
            }

        }
        // If allVideos is not empty and allVideos size it less than 10 - if person have viewed a few videos but not all
        else if (allVideos.size() > 0 && allVideos.size() < 10) {
            System.out.println("person has seen all videos but a few");
            System.out.println("allVideos is not empty && size < 10");
            // extract missing amount of videos
            int numberOfVideosToAdd = 10 - allVideos.size();

            System.out.println("");
            System.out.println("the allVideos size was: " + allVideos.size());
            System.out.println("we need to add: " + numberOfVideosToAdd);

            // get all videos
            List<Video> allVideos2 = videoService.findAllVideos();

            // delete the videos in allVideos2 that is already in allVideos
            for (Video video : allVideos) {
                allVideos2.remove(video);
            }

            // sort allVideos2 by most played
            List<Video> sortedAllVideos2 = sortAllVideosByPlays(allVideos2);

            // pick the remaining videos and add to allVideos
            for (int i = 0; i < numberOfVideosToAdd; i++) {
                allVideos.add(allVideos2.get(i));
            }

            // sort allVideos
            List<Video> sortAllVideos = sortAllVideosByPlays(allVideos);

            for (int i = 0; i < 10; i++) {
                top10videos.add(sortAllVideos.get(i));
            }
        }
        // person has seen all videos
        else if (allVideos.isEmpty()) { // if list is empty - return top 10 most listened pods no matter if user have listened to or not
            // Get all videos
            allVideos = videoService.findAllVideos();

            // sort videos by plays
            List<Video> sortedAllVideos = sortAllVideosByPlays(allVideos);

            // add to top 10
            for (int i = 0; i < 10; i++) {
                top10videos.add(sortedAllVideos.get(i));
            }

        }

        return top10videos;
    }

    public List<Pod> totalTop10Pods(User user) {
        // hämtar alla pods
        List<Pod> allPods = podService.findAllPods();

        List<Pod> podsToDelete = new ArrayList<>();

        System.out.println("");
        // lägg till alla pods user kollat på
        for (PlayedMedia playedMedia : user.getPlayedMedia()) {
            for (Pod pod : allPods) {
                if (playedMedia.getUrl().equalsIgnoreCase(pod.getUrl())) {
                    System.out.println("duplicate: " + pod.getTitle());
                    podsToDelete.add(pod);
                }
            }
        }

        System.out.println("");
        System.out.println("Pods thats been viewed/listened to and will be removed");
        for (Pod pod : podsToDelete) {
            System.out.println(pod.getTitle());
        }

        // deletar alla dubletter
        if (!podsToDelete.isEmpty()) {
            for (Pod pod : podsToDelete) {
                allPods.remove(pod);
            }
        }

        System.out.println("");
        System.out.println("those pods are now removed");
        System.out.println("size of allPods: " + allPods.size());


        List<Pod> top10pods = new ArrayList<>();

        // if lista is not empty - return most listened pods that user has not listened to
        if (allPods.size() >= 10) {
            List<Pod> sortedAllPods = sortAllPodsByPlays(allPods);


            // extract top 10
            for (int i = 0; i < 10; i++) {
                top10pods.add(sortedAllPods.get(i));
            }

        }
        // If allVideos is not empty and allVideos size it less than 10
        else if (allPods.size() > 0 && allPods.size() < 10) {
            System.out.println("person has seen all pods but a few");
            System.out.println("allPods is not empty && size < 10");
            // extract missing amount of videos
            int numberOfPodsToAdd = 10 - allPods.size();

            System.out.println("");
            System.out.println("the allPods size was: " + allPods.size());
            System.out.println("we need to add: " + numberOfPodsToAdd);

            // get all videos
            List<Pod> allPods2 = podService.findAllPods();

            // delete the videos in allVideos2 that is already in allVideos
            for (Pod pod : allPods) {
                allPods2.remove(pod);
            }

            // sort allVideos2 by most played
            List<Pod> sortedAllPods2 = sortAllPodsByPlays(allPods2);

            // pick the remaining videos and add to allVideos
            for (int i = 0; i < numberOfPodsToAdd; i++) {
                allPods.add(allPods2.get(i));
            }

            // sort allVideos
            List<Pod> sortAllPods = sortAllPodsByPlays(allPods);

            for (int i = 0; i < 10; i++) {
                top10pods.add(sortAllPods.get(i));
            }
        }
        // person has seen all videos
        else if (allPods.isEmpty()) { // if list is empty - return top 10 most listened pods no matter if user have listened to or not
            System.out.println("person has seen all pods");
            // Get all videos
            allPods = podService.findAllPods();

            // sort videos by plays
            List<Pod> sortedAllPods = sortAllPodsByPlays(allPods);

            // add to top 10
            for (int i = 0; i < 10; i++) {
                top10pods.add(sortedAllPods.get(i));
            }

        }

        return top10pods;
    }

    public List<Music> totalTop10Songs(User user) {
        System.out.println("in top 10 songs");
        // Sort users PlayerGenres by most played

        List<PlayedGenre> sortedPlayedGenres = sortAllPlayedGenresByPlays(user, "music");

        System.out.println("ENBART FÖR UTSKRIFT - KAN TA BORT DETTA:");
        System.out.println("");
        System.out.println("Users all played genres - sorted:");
        for (PlayedGenre playedGenre : sortedPlayedGenres) {
            System.out.println(playedGenre.getGenre() + " " + playedGenre.getTotalPlays());
        }

        System.out.println("topSongs - go into method");
        List<Music> topSongs = getTop8SongsFromUsersTopGenres(user, sortedPlayedGenres.size());

        int numberOfExtraSongs = 10 - topSongs.size();

        System.out.println("will add extra songs: " + numberOfExtraSongs);
        System.out.println("");
        System.out.println("the recommended 8 songs:");
        for (Music music : topSongs) {
            System.out.println(music.getPlayCounter() + " " + music.getTitle());
        }

        // TODO
        // Get all genres
        List<Genre> allGenres = getUnlistenedGenres(user, "music");

        System.out.println("");
        System.out.println("allGenres should now be lower: " + allGenres.size());

        System.out.println("inne här");
        // must add plays on regular genres to get most played genre not listened to.
        // sort by genre size
        List<Genre> sortedAllGenres = sortGenresByPlays(allGenres);

        // Take most played genre:
        Genre topGenre = sortedAllGenres.get(0);

        // Get list of Music from topGenre:
        List<Music> music = musicService.findAllMusicInGenre(topGenre);

        // IF MUSIC IS EMPTY - get from top played genres - this means there are no unlistened genres anymore.
        if (music.isEmpty()) {
            List<Genre> everyGenre = genreService.getAllGenres();
            List<Genre> sortedEveryGenre = sortGenresByPlays(everyGenre);
            Genre topListenedGenre = sortedEveryGenre.get(0);

            music = musicService.findAllMusicInGenre(topListenedGenre);

        }

        System.out.println("size of music: " + music.size());

        System.out.println("");
        System.out.println("testing if music is 0");
        for (Music music1 : music) {
            System.out.println(music1.getTitle());
        }
        System.out.println("");
        System.out.println("");

        // Sort music by plays
        List<Music> sortedMusic = sortAllMusicByPlays(music);

        // extract 2 songs from sorted music
        List<Music> musicToSendBack = new ArrayList<>();

        System.out.println("");
        System.out.println("all Unlistened genres:");
        for (Genre genre : allGenres) {
            System.out.println(genre.getGenre());
        }

        System.out.println("");
        System.out.println("Sorted Music:");
        for (Music song : sortedMusic) {
            System.out.println(song.getTitle());
        }

        System.out.println("");
        System.out.println("hej");
        for (int i = 0; i < numberOfExtraSongs; i++) {
            System.out.println("i: " + i + " / " + numberOfExtraSongs);
            musicToSendBack.add(sortedMusic.get(i));
        }
        System.out.println("topSongs size: " + topSongs.size());

        topSongs.addAll(musicToSendBack);

        List<Music> sortedTopSongs = sortAllMusicByPlays(topSongs);

        System.out.println("");
        System.out.println("SORTED TOP SONGS SHOULD BE 10");
        for (Music musicSongs : sortedTopSongs) {
            System.out.println(musicSongs.getPlayCounter() + " " + musicSongs.getTitle());
        }

        return sortedTopSongs;
    }

    public List<Music> getTop8SongsFromUsersTopGenres(User user, int size) {
        System.out.println("inside method - getting users top played genres");
        // Extract top 3 Genres
        List<PlayedGenre> usersTopPlayedGenres = getUsersMostPlayedGenresSortedByPlays(user, "music");
        System.out.println("");
        System.out.println("Extrect top 3 genres");

        System.out.println("going to converts PlayedGenres to Genre");
        // Convert PlayedGenres into Genres
        List<Genre> topGenres = convertUserPlayedGenresToGenre(usersTopPlayedGenres);
        System.out.println("convering top genres");

        List<Music> topSongs = new ArrayList<>();

        if (size >= 3) {
            // Get all songs of genre
            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));
            List<Music> allSongsSecondGenre = musicService.findAllMusicInGenre(topGenres.get(1));
            List<Music> allSongsThirdGenre = musicService.findAllMusicInGenre(topGenres.get(2));

            System.out.println("");
            System.out.println("INFO");
            for (Music music : allSongsFirstGenre) {
                System.out.println(music.getPlayCounter() + " " + music.getTitle());
            }
            System.out.println("");
            System.out.println("");

            // Add all songs into 1 list
            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);
            allSongsTogether.addAll(allSongsSecondGenre);
            allSongsTogether.addAll(allSongsThirdGenre);

            System.out.println("");
            System.out.println("allSongsTogether");
            for (Music music : allSongsTogether) {
                System.out.println(music.getPlayCounter() + " " + music.getTitle());
            }

            List<Music> musicToDelete = new ArrayList<>();

            // Add songs to list that already been listened to
            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        System.out.println("deleting: " + music.getTitle());
                        musicToDelete.add(music);
                    }
                }
            }

            System.out.println("");
            System.out.println("the songs user listened to:");
            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                System.out.println(playedMedia.getTitle());
            }
            System.out.println("---");
            System.out.println("songs to be deleted:");
            for (Music music : musicToDelete) {
                System.out.println(music.getTitle());
            }

            // Remove the music thats already been listened to
            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            // If allSongsTogether = empty it means no unplayed songs exist. If so, add all top 3 genre songs into allSongsTogether
            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
                allSongsTogether.addAll(allSongsSecondGenre);
                allSongsTogether.addAll(allSongsThirdGenre);
            }

            // Sort list with music user has not yet listened to
            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            // Extract top 8 songs and put into list.
            // if all songs are more than 8 extract 8.
            System.out.println("size: " + sortedAllSongs.size());
            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
            // If all songs are less than 8, extract how many there are
            else if (sortedAllSongs.size() < 8) {
                for (int i = 0; i < sortedAllSongs.size(); i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
        } else if (size == 2) {
            // Get all songs of genre
            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));
            List<Music> allSongsSecondGenre = musicService.findAllMusicInGenre(topGenres.get(1));

            // Add all songs into 1 list
            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);
            allSongsTogether.addAll(allSongsSecondGenre);

            List<Music> musicToDelete = new ArrayList<>();

            // Add songs to list that already been listened to
            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        musicToDelete.add(music);
                    }
                }
            }

            // Remove the music thats already been listened to
            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            // If allSongsTogether = empty it means no unplayed songs exist. If so, add all top 2 genre songs into allSongsTogether
            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
                allSongsTogether.addAll(allSongsSecondGenre);
            }

            // Sort list with music user has not yet listened to
            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            // Extract top 8 songs and put into list.
            // if all songs are more than 8 extract 8.
            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
            // If all songs are less than 8, extract how many there are
            else if (sortedAllSongs.size() < 8) {
                for (int i = 0; i < sortedAllSongs.size(); i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
        } else if (size == 1) {
            // Get all songs of genre
            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));

            // Add all songs into 1 list
            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);

            List<Music> musicToDelete = new ArrayList<>();

            // Add songs to list that already been listened to
            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        musicToDelete.add(music);
                    }
                }
            }

            // Remove the music thats already been listened to
            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            // If allSongsTogether = empty it means no unplayed songs exist. If so, add all top 1 genre songs into allSongsTogether
            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
            }

            // Sort list with music user has not yet listened to
            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            // Extract top 8 songs and put into list.
            // if all songs are more than 8 extract 8.
            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
            // If all songs are less than 8, extract how many there are
            else if (sortedAllSongs.size() < 8) {
                for (int i = 0; i < sortedAllSongs.size(); i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }

        }
        if (size == 0) {
            return topSongs;

        }


        return topSongs;
    }

    public List<Genre> getUnlistenedGenres(User user, String type) {
        List<Genre> allGenres = genreService.getAllGenres();
        List<PlayedGenre> userGenres = user.getPlayedGenre();

        // If type is Music - remove all other genres
        if (type.equalsIgnoreCase("music")) {
            List<Genre> nonMusicGenres = new ArrayList<>();

            for (Genre genre : allGenres) {
                if (!genre.getType().equalsIgnoreCase("music")) {
                    nonMusicGenres.add(genre);
                }
            }

            for (Genre genre : nonMusicGenres) {
                allGenres.remove(genre);
            }

        }
        // If type is Pod - remove all other genres
        if (type.equalsIgnoreCase("pod")) {
            List<Genre> nonPodGenres = new ArrayList<>();

            for (Genre genre : allGenres) {
                if (!genre.getType().equalsIgnoreCase("pod")) {
                    nonPodGenres.add(genre);
                }
            }

            for (Genre genre : nonPodGenres) {
                allGenres.remove(genre);
            }
        }
        // If type is Video - remove all other genres
        if (type.equalsIgnoreCase("video")) {
            List<Genre> nonVideoGenres = new ArrayList<>();

            for (Genre genre : allGenres) {
                if (!genre.getType().equalsIgnoreCase("video")) {
                    nonVideoGenres.add(genre);
                }
            }

            for (Genre genre : nonVideoGenres) {
                allGenres.remove(genre);
            }
        }

        List<Genre> genresToDelete = new ArrayList<>();

        for (PlayedGenre playedGenre : userGenres) {
            for (Genre genre : allGenres) {
                if (playedGenre.getGenre().equalsIgnoreCase(genre.getGenre())) {
                    genresToDelete.add(genre);
                }
            }
        }

        for (Genre genre : genresToDelete) {
            allGenres.remove(genre);
        }

        List<Genre> sortedAllGenres = sortGenresByPlays(allGenres);

        return sortedAllGenres;
    }


}
