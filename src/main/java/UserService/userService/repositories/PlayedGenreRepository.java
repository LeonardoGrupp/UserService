package UserService.userService.repositories;

import UserService.userService.entites.PlayedGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayedGenreRepository extends JpaRepository<PlayedGenre, Long> {
}
