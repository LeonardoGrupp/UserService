package UserService.userService.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Embeddable
public class Genre {

    private long id;
    private String genre;
    private int totalLikes;
    private int totalPlays;

    public Genre() {
    }

    public Genre(String genre, int totalLikes, int totalPlays) {
        this.genre = genre;
        this.totalLikes = totalLikes;
        this.totalPlays = totalPlays;
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
}
