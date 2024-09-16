package UserService.userService.entites;

import UserService.userService.vo.Genre;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "played_media")
public class PlayedMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;
    private String title;
    private String url;
    private String releaseDate;
    private int timesPlayed;
//    @ManyToMany
//    @JoinTable(
//            name = "played_media_genres",
//            joinColumns = @JoinColumn(name = "played_media_id"),
//            inverseJoinColumns = @JoinColumn(name = "genre_id")
//    )
//    private List<Genre> genres = new ArrayList<>();

    public PlayedMedia() {
    }

    public PlayedMedia(String type, String title, String url, String releaseDate) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.releaseDate = releaseDate;
        this.timesPlayed += 1;
    }

    public PlayedMedia(String type, String title, String url, String releaseDate, List<Genre> genres) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.releaseDate = releaseDate;
        this.timesPlayed += 1;
//        this.genres = genres;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public void countPlay() {
        timesPlayed += 1;
    }

//    public List<Genre> getGenres() {
//        return genres;
//    }
//
//    public void setGenres(List<Genre> genres) {
//        this.genres = genres;
//    }
}
