package UserService.userService.vo;

import jakarta.persistence.*;

@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String genre;

    public Genre() {
    }

    public Genre(String genre) {
        this.genre = genre;
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
}
