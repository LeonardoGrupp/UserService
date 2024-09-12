package UserService.userService.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public class Album {

    private long id;
    private String name;

    public Album() {
    }

    public Album(String name) {
        this.name = name;
    }

    public Album(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
