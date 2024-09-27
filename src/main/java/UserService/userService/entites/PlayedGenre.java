package UserService.userService.entites;

import jakarta.persistence.*;

@Entity
@Table(name = "played_genres")
public class PlayedGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String genre;
    private String type;
    @Column(name = "total_plays")
    private int totalPlays;
    @Column(name = "is_liked")
    private boolean isLiked;
    @Column(name = "is_disliked")
    private boolean isDisliked;

    public PlayedGenre() {
    }

    public PlayedGenre(String genre, String type) {
        this.genre = genre;
        this.type = type;
        this.totalPlays += 1;
        this.isLiked = false;
        this.isDisliked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }

    public void countPlay() {
        this.totalPlays += 1;
    }

    public void likeGenre() {
        this.isDisliked = false;
        this.isLiked = true;
    }
    public void disLikeGenre() {
        this.isLiked = false;
        this.isDisliked = true;
    }

    public void resetLikeAndDisLikeGenre() {
        this.isLiked = false;
        this.isDisliked = false;
    }
}
