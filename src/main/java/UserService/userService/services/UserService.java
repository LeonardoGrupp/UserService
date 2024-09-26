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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
                        System.out.println("genre was found - adding +1");

                        // add +1 in play
                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre())) {
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

                    Genre genreToPlay = genreService.findGenreByGenre(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                    System.out.println("the genre has been counted play");
                }
                System.out.println("music has been counted play");

                return playedMediaService.save(mediaBeenPlayed);
            }
        } else if (isPod(url)) {
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
        } else if (isVideo(url)) {
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

    public PlayedMedia likeMedia(long id, String url) {
        // Get User
        User user = findUserById(id);
        System.out.println("finding user");


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        if (!hasPlayedMediaBefore(user, url)) {
            System.out.println("ERROR: In order to like the media, you first have to play it");
            return null;

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            if (usersPlayedMedia == null || usersPlayedMedia.isEmpty()) {
                System.out.println("ERROR: Users playedMedia was empty");
                return null;
            }

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equals(url)) {
                    if (playedMedia.isLiked() && user.getLikedMedia().contains(playedMedia)) {
                        System.out.println("Media already liked - doing nothing");
                        return playedMedia;
                    } else {

                        playedMedia.likeMedia();

                        playedMediaService.save(playedMedia);

                        System.out.println("media has been liked");

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);

                        userRepository.save(user);

                        return playedMedia;
                    }

                }
            }
            System.out.println("ERROR: kept going even though it shouldnt have");
            return null;
        }
    }

    public PlayedGenre likeGenre(long id, String genreName) {
        User user = findUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName)) {
                System.out.println("Liking played genre: " + genreName);
                playedGenre.likeGenre();

                System.out.println("Saving played genre");
                playedGenreService.save(playedGenre);

                System.out.println("user.removeOrAddGenreFromDislikedandLikedGenre");
                user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);

                System.out.println("saving user");
                userRepository.save(user);

                System.out.println("returning played genre");
                return playedGenre;
            }
        }
        System.out.println("ERROR: kept going even though it shouldnt have");
        return null;
    }

    public PlayedMedia disLikeMedia(long id, String url) {
        // Get User
        User user = findUserById(id);
        System.out.println("finding user");


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        if (!hasPlayedMediaBefore(user, url)) {
            System.out.println("ERROR: In order to dislike the media, you first have to play it");
            return null;

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            if (usersPlayedMedia == null || usersPlayedMedia.isEmpty()) {
                System.out.println("ERROR: Users playedMedia was empty");
                return null;
            }

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equals(url)) {
                    if (playedMedia.isDisliked() && user.getDisLikedMedia().contains(playedMedia)) {
                        System.out.println("Media already disliked - doing nothing");
                        return playedMedia;
                    } else {
                        playedMedia.disLikeMedia();

                        playedMediaService.save(playedMedia);

                        System.out.println("media has been disliked");

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);

                        userRepository.save(user);

                        return playedMedia;
                    }
                }
            }
            System.out.println("ERROR: kept going even though it shouldnt have");
            return null;
        }
    }

    public PlayedGenre disLikeGenre(long id, String genreName) {
        User user = findUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName)) {
                System.out.println("Disliking played genre: " + genreName);
                playedGenre.disLikeGenre();

                System.out.println("Saving played genre");
                playedGenreService.save(playedGenre);

                System.out.println("user.removeOrAddGenreFromDislikedandLikedGenre");
                user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);

                System.out.println("saving user");
                userRepository.save(user);

                System.out.println("returning played genre");
                return playedGenre;
            }
        }
        System.out.println("ERROR: kept going even though it shouldnt have");
        return null;
    }

    public PlayedMedia resetLikesAndDisLikesOfMedia(long id, String url) {
        // Get User
        User user = findUserById(id);
        System.out.println("finding user");


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        if (!hasPlayedMediaBefore(user, url)) {
            System.out.println("ERROR: In order to reset likes and dislike of the media, you first have to play it");
            return null;

        } else {

            List<PlayedMedia> usersPlayedMedia = user.getPlayedMedia();

            if (usersPlayedMedia == null || usersPlayedMedia.isEmpty()) {
                System.out.println("ERROR: Users playedMedia was empty");
                return null;
            }

            for (PlayedMedia playedMedia : usersPlayedMedia) {
                if (playedMedia.getUrl().equals(url)) {
                    if (!playedMedia.isLiked() && !playedMedia.isDisliked()) {
                        System.out.println("Media is already false on both like and dislike - doing nothing");
                        return playedMedia;
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
            System.out.println("ERROR: kept going even though it shouldnt have");
            return null;
        }
    }

    public PlayedGenre resetLikesAndDisLikesOfGenre(long id, String genreName) {
        User user = findUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        for (PlayedGenre playedGenre : user.getPlayedGenre()) {
            if (playedGenre.getGenre().equalsIgnoreCase(genreName)) {
                if (!playedGenre.isLiked() && !playedGenre.isDisliked()) {
                    System.out.println("Genre is already false on both like and dislike - doing nothing");
                    return playedGenre;
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
        System.out.println("ERROR: kept going even though it shouldnt have");
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

    public List<Music> recommendations(long id) {
        User user = findUserById(id);

        if (user == null) {
            System.out.println("USER NULL");
            return null;
        }

        return totalTop10Songs(user);
    }

    public List<PlayedGenre> getUsersMostPlayedGenresSortedByPlays(User user) {
        List<PlayedGenre> usersGenres = user.getPlayedGenre();

        List<PlayedGenre> sortedFullGenreList = usersGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());

        List<PlayedGenre> sortedTopGenreList = new ArrayList<>();
        if (sortedFullGenreList.size() >= 3) {
            for (int i = 0; i < 3; i++) {
                sortedTopGenreList.add(sortedFullGenreList.get(i));
            }
        } else if (sortedFullGenreList.size() < 3) {
            for (int i = 0; i < sortedFullGenreList.size(); i++) {
                sortedTopGenreList.add(sortedFullGenreList.get(i));
            }
        }


        return sortedTopGenreList;
    }

    public List<PlayedGenre> sortAllPlayedGenresByPlays(User user) {
        List<PlayedGenre> usersGenres = user.getPlayedGenre();

        List<PlayedGenre> sortedGenreList = usersGenres.stream().sorted(Comparator.comparingInt(PlayedGenre::getTotalPlays).reversed()).collect(Collectors.toList());

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

    public List<Genre> convertUserPlayedGenresToGenre(List<PlayedGenre> playedGenreList) {

        List<Genre> topGenres = new ArrayList<>();
        for (PlayedGenre playedGenre : playedGenreList) {
            topGenres.add(genreService.findGenreByGenre(playedGenre.getGenre()));
        }

        return topGenres;
    }

    public List<Music> totalTop10Songs(User user) {
        // Sort users PlayerGenres by most played
        List<PlayedGenre> sortedPlayedGenres = sortAllPlayedGenresByPlays(user);
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
        List<Genre> allGenres = getUnlistenedGenres(user);

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

        // TODO IF MUSIC IS EMPTY - get from top played genres - this means there are no unlistened genres anymore.
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

        //TODO Se så att den fortfarande hittar låtar, även om du lyssnat på alla genres

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
        List<PlayedGenre> usersTopPlayedGenres = getUsersMostPlayedGenresSortedByPlays(user);
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

    public List<Genre> getUnlistenedGenres(User user) {
        List<Genre> allGenres = genreService.getAllGenres();
        List<PlayedGenre> userGenres = user.getPlayedGenre();

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
