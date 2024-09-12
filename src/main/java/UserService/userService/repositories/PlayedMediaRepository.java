package UserService.userService.repositories;

import UserService.userService.entites.PlayedMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayedMediaRepository extends JpaRepository<PlayedMedia, Long> {
}
