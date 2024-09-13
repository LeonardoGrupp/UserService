package UserService.userService.entites;

import jakarta.persistence.*;

@Entity
@Table(name = "played_genres")
public class PlayedGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String genre;
    private int totalPlays;

    public PlayedGenre() {
    }

    public PlayedGenre(String genre) {
        this.genre = genre;
        this.totalPlays += 1;
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

    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    public void countPlay() {
        this.totalPlays += 1;
    }
}
