package UserService.userService.repositories;

import UserService.userService.vo.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    Optional<Music> findByUrlIgnoreCase(String url);
    boolean existsByUrlIgnoreCase(String url);
}
