package UserService.userService.service;

import UserService.userService.entities.User;
import UserService.userService.exception.UserNotFoundException;
import UserService.userService.repository.MusicRepository;
import UserService.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Transactional
    public void likeMedia(long userId, long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        user.getDislikedMedia().remove(mediaId);

        if (!user.getLikedMedia().contains(mediaId)) {
            user.getLikedMedia().add(mediaId);
        }

        userRepository.save(user);
    }

    @Transactional
    public void dislikeMedia(long userId, long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        user.getLikedMedia().remove(mediaId);

        if (!user.getDislikedMedia().contains(mediaId)) {
            user.getDislikedMedia().add(mediaId);
        }

        userRepository.save(user);
    }

    @Transactional
    public List<Long> getLikedMedia(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        return user.getLikedMedia();
    }
}
