package UserService.userService.entites;

import UserService.userService.vo.Media;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public void addMediaToPlayedMedia(PlayedMedia media) {
        playedMedia.add(media);
    }
}
