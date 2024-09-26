package UserService.userService.repositories;

import UserService.userService.vo.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Genre findGenreByGenreIgnoreCase(String genre);
}
