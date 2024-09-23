package UserService.userService.repositories;

import UserService.userService.vo.Pod;
import UserService.userService.vo.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PodRepository extends JpaRepository<Pod, Long> {
    Optional<Pod> findByUrlIgnoreCase(String url);
    boolean existsByUrlIgnoreCase(String url);
}
