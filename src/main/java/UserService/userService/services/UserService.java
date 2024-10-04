package UserService.userService.services;

import UserService.userService.entites.PlayedGenre;
import UserService.userService.entites.PlayedMedia;
import UserService.userService.entites.User;
import UserService.userService.repositories.UserRepository;
import UserService.userService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
        User user = findUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User not found with ID: " + id);
        }

        if (isMusic(url)) {

            if (!hasPlayedMediaBefore(user, url)) {

                Music mediaToPlay = getMusicByUrl(url);

                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                List<PlayedGenre> playedGenreList = new ArrayList<>();

                for (Genre genre : mediaToPlay.getGenres()) {

                    if (!hasPlayedMusicGenreBefore(user, genre)) {

                        PlayedGenre playedGenre = playedGenreService.createFromMusicGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {

                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenreOfUser.getType().equalsIgnoreCase("music")) {
                                playedGenre = playedGenreOfUser;
                            }
                        }

                        if (playedGenre == null) {
                            return null;
                        }

                        genreService.addPlay(genre);

                        playedGenre.countPlay();

                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);
                    }
                }

                PlayedMedia savedMedia;

                if (playedGenreList.isEmpty()) {
                    savedMedia = playedMediaService.createMusicFromUser(mediaToPlay);
                } else {
                    savedMedia = playedMediaService.createMusicFromUserWithList(mediaToPlay, playedGenreList);
                }

                musicService.addPlay(mediaToPlay);

                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;

            } else {
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                Music mediaToPlay = getMusicByUrl(url);
                musicService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);

                    Genre genreToPlay = genreService.findGenreByGenreTypeMusic(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                }

                return playedMediaService.save(mediaBeenPlayed);
            }
        } else if (isPod(url)) {

            if (!hasPlayedMediaBefore(user, url)) {
                Pod mediaToPlay = getPodByUrl(url);

                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                List<PlayedGenre> playedGenreList = new ArrayList<>();

                for (Genre genre : mediaToPlay.getGenres()) {
                    System.out.println(genre.getGenre());

                    if (!hasPlayedPodGenreBefore(user, genre)) {

                        PlayedGenre playedGenre = playedGenreService.createFromPodGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {

                        PlayedGenre playedGenre = null;
                        for (PlayedGenre playedGenreOfUser : user.getPlayedGenre()) {
                            if (playedGenreOfUser.getGenre().equalsIgnoreCase(genre.getGenre()) && playedGenreOfUser.getType().equalsIgnoreCase("pod")) {
                                playedGenre = playedGenreOfUser;
                            }
                        }

                        if (playedGenre == null) {
                            return null;
                        }

                        genreService.addPlay(genre);

                        playedGenre.countPlay();
                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);
                    }
                }

                PlayedMedia savedMedia;

                if (playedGenreList.isEmpty()) {

                    savedMedia = playedMediaService.createPodFromUser(mediaToPlay);
                } else {

                    savedMedia = playedMediaService.createPodFromUserWithList(mediaToPlay, playedGenreList);
                }

                podService.addPlay(mediaToPlay);

                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;

            } else {
                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                Pod mediaToPlay = getPodByUrl(url);
                podService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);

                    Genre genreToPlay = genreService.findGenreByGenreTypePod(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                }

                return playedMediaService.save(mediaBeenPlayed);
            }
        } else if (isVideo(url)) {

            if (!hasPlayedMediaBefore(user, url)) {
                Video mediaToPlay = getVideoByUrl(url);

                if (mediaToPlay == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Media not found with URL: " + url);
                }

                List<PlayedGenre> playedGenreList = new ArrayList<>();

                for (Genre genre : mediaToPlay.getGenres()) {

                    if (!hasPlayedVideoGenreBefore(user, genre)) {
                        PlayedGenre playedGenre = playedGenreService.createFromVideoGenres(genre);
                        genreService.addPlay(genre);

                        user.addGenreToPlayedGenre(playedGenre);
                        userRepository.save(user);

                        playedGenreList.add(playedGenre);

                    } else {
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

                        playedGenre.countPlay();

                        PlayedGenre savedGenre = playedGenreService.save(playedGenre);

                        playedGenreList.add(savedGenre);
                    }
                }

                PlayedMedia savedMedia;
                if (playedGenreList.isEmpty()) {
                    savedMedia = playedMediaService.createVideoFromUser(mediaToPlay);
                } else {
                    savedMedia = playedMediaService.createVideoFromUserWithList(mediaToPlay, playedGenreList);
                }

                videoService.addPlay(mediaToPlay);

                user.addMediaToPlayedMedia(savedMedia);
                userRepository.save(user);

                return savedMedia;

            } else {

                PlayedMedia mediaBeenPlayed = getMediaFromUsersMediaList(user, url);

                Video mediaToPlay = getVideoByUrl(url);
                videoService.addPlay(mediaToPlay);

                mediaBeenPlayed.countPlay();
                for (PlayedGenre genre : mediaBeenPlayed.getGenres()) {
                    genre.countPlay();
                    playedGenreService.save(genre);

                    Genre genreToPlay = genreService.findGenreByGenreTypeVideo(genre.getGenre());
                    genreService.addPlay(genreToPlay);
                }

                return playedMediaService.save(mediaBeenPlayed);
            }
        }

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

                        user.removeOrAddMediaFromDislikedAndLikedMedia(playedMedia);
                        userRepository.save(user);

                        return playedMedia;
                    }
                }
            }

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

                    user.removeOrAddGenreFromDislikedAndLikedGenre(playedGenre);
                    userRepository.save(user);

                    return playedGenre;
                }
            }
        }

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
            return true;
        } else {
            return false;
        }
    }

    public boolean isVideo(String url) {

        boolean exists = videoService.videoExistsByUrl(url);

        if (exists) {
            return true;
        } else {
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

            List<PlayedGenre> likedUserMusicGenres = new ArrayList<>();
            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isLiked()) {
                    likedUserMusicGenres.add(playedGenre);
                }
            }

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
        List<PlayedGenre> usersGenres = user.getPlayedGenre();

        List<PlayedGenre> userGenresToSendBack = new ArrayList<>();

        if (type.equals("music")) {

            for (PlayedGenre playedGenre : usersGenres) {
                if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isLiked()) {
                    userGenresToSendBack.add(playedGenre);
                }
            }

            if (userGenresToSendBack.isEmpty()) {

                for (PlayedGenre playedGenre : usersGenres) {
                    if (playedGenre.getType().equals("music") && !playedGenre.isDisliked()) {

                        userGenresToSendBack.add(playedGenre);
                    }
                }
            }

            if (!userGenresToSendBack.isEmpty()) {

                for (PlayedGenre playedGenre : usersGenres) {
                    if (playedGenre.getType().equalsIgnoreCase("music") && playedGenre.isDisliked()) {
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

    public List<Genre> convertUserPlayedGenresToGenre(List<PlayedGenre> playedGenreList, String type) {

        List<Genre> topGenres = new ArrayList<>();
        for (PlayedGenre playedGenre : playedGenreList) {
            if (type.equalsIgnoreCase("music")) {
                Genre genre = genreService.findGenreByGenreTypeMusic(playedGenre.getGenre());

                if (genre != null) {
                    topGenres.add(genre);
                }
            } else if (type.equalsIgnoreCase("pod")) {
                Genre genre = genreService.findGenreByGenreTypePod(playedGenre.getGenre());

                if (genre != null) {
                    topGenres.add(genre);
                }
            } else if (type.equalsIgnoreCase("video")) {
                Genre genre = genreService.findGenreByGenreTypeVideo(playedGenre.getGenre());

                if (genre != null) {
                    topGenres.add(genre);
                }
            }
        }

        return topGenres;
    }

    public List<Video> totalTop10Videos(User user) {

        List<Video> allVideos = videoService.findAllVideos();

        List<Video> videosToDelete = new ArrayList<>();

        for (PlayedMedia playedMedia : user.getPlayedMedia()) {
            for (Video video : allVideos) {
                if (playedMedia.getUrl().equalsIgnoreCase(video.getUrl())) {
                    videosToDelete.add(video);
                }
            }
        }

        if (!videosToDelete.isEmpty()) {
            for (Video video : videosToDelete) {
                allVideos.remove(video);
            }
        }

        List<Video> top10videos = new ArrayList<>();

        if (allVideos.size() >= 10) {
            List<Video> sortedAllVideos = sortAllVideosByPlays(allVideos);

            for (int i = 0; i < 10; i++) {
                top10videos.add(sortedAllVideos.get(i));
            }

        }
        else if (allVideos.size() > 0 && allVideos.size() < 10) {

            int numberOfVideosToAdd = 10 - allVideos.size();

            List<Video> allVideos2 = videoService.findAllVideos();

            for (Video video : allVideos) {
                allVideos2.remove(video);
            }

            List<Video> sortedAllVideos2 = sortAllVideosByPlays(allVideos2);

            for (int i = 0; i < numberOfVideosToAdd; i++) {
                allVideos.add(allVideos2.get(i));
            }

            List<Video> sortAllVideos = sortAllVideosByPlays(allVideos);

            for (int i = 0; i < 10; i++) {
                top10videos.add(sortAllVideos.get(i));
            }
        }

        else if (allVideos.isEmpty()) {
            allVideos = videoService.findAllVideos();

            List<Video> sortedAllVideos = sortAllVideosByPlays(allVideos);

            for (int i = 0; i < 10; i++) {
                top10videos.add(sortedAllVideos.get(i));
            }

        }

        return top10videos;
    }

    public List<Pod> totalTop10Pods(User user) {

        List<Pod> allPods = podService.findAllPods();

        List<Pod> podsToDelete = new ArrayList<>();

        for (PlayedMedia playedMedia : user.getPlayedMedia()) {
            for (Pod pod : allPods) {
                if (playedMedia.getUrl().equalsIgnoreCase(pod.getUrl())) {
                    podsToDelete.add(pod);
                }
            }
        }

        if (!podsToDelete.isEmpty()) {
            for (Pod pod : podsToDelete) {
                allPods.remove(pod);
            }
        }

        List<Pod> top10pods = new ArrayList<>();

        if (allPods.size() >= 10) {
            List<Pod> sortedAllPods = sortAllPodsByPlays(allPods);

            for (int i = 0; i < 10; i++) {
                top10pods.add(sortedAllPods.get(i));
            }

        }

        else if (allPods.size() > 0 && allPods.size() < 10) {

            int numberOfPodsToAdd = 10 - allPods.size();

            List<Pod> allPods2 = podService.findAllPods();

            for (Pod pod : allPods) {
                allPods2.remove(pod);
            }

            List<Pod> sortedAllPods2 = sortAllPodsByPlays(allPods2);

            for (int i = 0; i < numberOfPodsToAdd; i++) {
                allPods.add(allPods2.get(i));
            }

            List<Pod> sortAllPods = sortAllPodsByPlays(allPods);

            for (int i = 0; i < 10; i++) {
                top10pods.add(sortAllPods.get(i));
            }
        }
        else if (allPods.isEmpty()) {
            allPods = podService.findAllPods();

            List<Pod> sortedAllPods = sortAllPodsByPlays(allPods);

            for (int i = 0; i < 10; i++) {
                top10pods.add(sortedAllPods.get(i));
            }
        }

        return top10pods;
    }

    public List<Music> totalTop10Songs(User user) {
        List<PlayedGenre> sortedPlayedGenres = sortAllPlayedGenresByPlays(user, "music");

        List<Music> topSongs = getTop8SongsFromUsersTopGenres(user, sortedPlayedGenres.size());

        int numberOfExtraSongs = 10 - topSongs.size();

        List<Genre> allGenres = getUnlistenedGenres(user, "music");

        List<Genre> sortedAllGenres = sortGenresByPlays(allGenres);

        Genre topGenre = sortedAllGenres.get(0);

        List<Music> music = musicService.findAllMusicInGenre(topGenre);

        if (music.isEmpty()) {
            List<Genre> everyGenre = genreService.getAllGenres();
            List<Genre> sortedEveryGenre = sortGenresByPlays(everyGenre);
            Genre topListenedGenre = sortedEveryGenre.get(0);

            music = musicService.findAllMusicInGenre(topListenedGenre);

        }

        List<Music> sortedMusic = sortAllMusicByPlays(music);

        List<Music> musicToSendBack = new ArrayList<>();

        for (int i = 0; i < numberOfExtraSongs; i++) {
            musicToSendBack.add(sortedMusic.get(i));
        }

        topSongs.addAll(musicToSendBack);

        List<Music> sortedTopSongs = sortAllMusicByPlays(topSongs);

        return sortedTopSongs;
    }

    public List<Music> getTop8SongsFromUsersTopGenres(User user, int size) {
        List<PlayedGenre> usersTopPlayedGenres = getUsersMostPlayedGenresSortedByPlays(user, "music");

        List<Genre> topGenres = convertUserPlayedGenresToGenre(usersTopPlayedGenres, "music");

        List<Music> topSongs = new ArrayList<>();

        if (size >= 3) {
            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));
            List<Music> allSongsSecondGenre = musicService.findAllMusicInGenre(topGenres.get(1));
            List<Music> allSongsThirdGenre = musicService.findAllMusicInGenre(topGenres.get(2));

            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);
            allSongsTogether.addAll(allSongsSecondGenre);
            allSongsTogether.addAll(allSongsThirdGenre);

            List<Music> musicToDelete = new ArrayList<>();

            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        musicToDelete.add(music);
                    }
                }
            }

            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
                allSongsTogether.addAll(allSongsSecondGenre);
                allSongsTogether.addAll(allSongsThirdGenre);
            }

            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
            else if (sortedAllSongs.size() < 8) {
                for (int i = 0; i < sortedAllSongs.size(); i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
        } else if (size == 2) {

            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));
            List<Music> allSongsSecondGenre = musicService.findAllMusicInGenre(topGenres.get(1));

            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);
            allSongsTogether.addAll(allSongsSecondGenre);

            List<Music> musicToDelete = new ArrayList<>();

            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        musicToDelete.add(music);
                    }
                }
            }

            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
                allSongsTogether.addAll(allSongsSecondGenre);
            }

            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }

            else if (sortedAllSongs.size() < 8) {
                for (int i = 0; i < sortedAllSongs.size(); i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }
        } else if (size == 1) {
            List<Music> allSongsFirstGenre = musicService.findAllMusicInGenre(topGenres.get(0));

            List<Music> allSongsTogether = new ArrayList<>();
            allSongsTogether.addAll(allSongsFirstGenre);

            List<Music> musicToDelete = new ArrayList<>();

            for (PlayedMedia playedMedia : user.getPlayedMedia()) {
                for (Music music : allSongsTogether) {
                    if (playedMedia.getUrl().equalsIgnoreCase(music.getUrl())) {
                        musicToDelete.add(music);
                    }
                }
            }

            for (Music music : musicToDelete) {
                allSongsTogether.remove(music);
            }

            if (allSongsTogether.isEmpty()) {
                allSongsTogether.addAll(allSongsFirstGenre);
            }

            List<Music> sortedAllSongs = sortAllMusicByPlays(allSongsTogether);

            if (sortedAllSongs.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    topSongs.add(sortedAllSongs.get(i));
                }
            }

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
