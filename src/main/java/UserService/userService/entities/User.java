package UserService.userService.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @ElementCollection
    private List<Long> likedMedia = new ArrayList<>();

    @ElementCollection
    private List<Long> dislikedMedia = new ArrayList<>();

    public User() {}

    public User(Long id, String username, List<Long> likedMedia, List<Long> dislikedMedia) {
        this.id = id;
        this.username = username;
        this.likedMedia = likedMedia;
        this.dislikedMedia = dislikedMedia;
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

    public List<Long> getLikedMedia() {
        return likedMedia;
    }

    public void setLikedMedia(List<Long> likedMedia) {
        this.likedMedia = likedMedia;
    }

    public List<Long> getDislikedMedia() {
        return dislikedMedia;
    }

    public void setDislikedMedia(List<Long> dislikedMedia) {
        this.dislikedMedia = dislikedMedia;
    }
}