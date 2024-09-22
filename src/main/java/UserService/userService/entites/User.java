package UserService.userService.entites;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;

    @OneToMany
    private List<PlayedGenre> playedGenre;
    @OneToMany
    private List<PlayedMedia> likedMedia;
    @OneToMany
    private List<PlayedMedia> disLikedMedia;
    @OneToMany
    private List<PlayedMedia> playedMedia;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.likedMedia = new ArrayList<>();
        this.disLikedMedia = new ArrayList<>();
        this.playedMedia = new ArrayList<>();
    }

    public User(long id, String username) {
        this.id = id;
        this.username = username;
        this.likedMedia = new ArrayList<>();
        this.disLikedMedia = new ArrayList<>();
        this.playedMedia = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PlayedMedia> getLikedMedia() {
        return likedMedia;
    }

    public void setLikedMedia(List<PlayedMedia> likedMedia) {
        this.likedMedia = likedMedia;
    }

    public List<PlayedMedia> getDisLikedMedia() {
        return disLikedMedia;
    }

    public void setDisLikedMedia(List<PlayedMedia> disLikedMedia) {
        this.disLikedMedia = disLikedMedia;
    }

    public List<PlayedMedia> getPlayedMedia() {
        return playedMedia;
    }

    public void setPlayedMedia(List<PlayedMedia> playedMedia) {
        this.playedMedia = playedMedia;
    }

    public List<PlayedGenre> getPlayedGenre() {
        return playedGenre;
    }

    public void setPlayedGenre(List<PlayedGenre> playedGenre) {
        this.playedGenre = playedGenre;
    }

    public void addMediaToPlayedMedia(PlayedMedia media) {
        playedMedia.add(media);
    }

    public void addGenreToPlayedGenre(PlayedGenre genre) { playedGenre.add(genre); }

    public void removeOrAddMediaFromDislikedAndLikedMedia(PlayedMedia media) {
        // If media is NOT disliked and is in dislike-list - REMOVE
        if (!media.isDisliked() && disLikedMedia.contains(media)) {
            disLikedMedia.remove(media);
        }
        // if media IS disliked but NOT in dislike-list - ADD
        if (media.isDisliked() && !disLikedMedia.contains(media)) {
            disLikedMedia.add(media);
        }
        // If Media is NOT liked and is in like-list - REMOVE
        if (!media.isLiked() && likedMedia.contains(media)) {
            likedMedia.remove(media);
        }
        // If Media IS liked but NOT in like-list - ADD
        if (media.isLiked() && !likedMedia.contains(media)) {
            likedMedia.add(media);
        }
    }
}
