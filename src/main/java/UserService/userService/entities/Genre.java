package UserService.userService.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "music_genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String genre;
    private int totalLikes;
    private int totalPlays;

    public Genre() {
    }

    public Genre(String genre) {
        this.genre = genre;
        this.totalLikes = 0;
        this.totalPlays = 0;
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

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    public void countPlay() {
        this.totalPlays += 1;
    }

    public void addLike() {
        this.totalLikes += 1;
    }
}
