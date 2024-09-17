package UserService.userService.service;

import UserService.userService.entities.Music;
import UserService.userService.entities.User;
import UserService.userService.exception.MediaNotFoundException;
import UserService.userService.exception.UserNotFoundException;
import UserService.userService.repository.MusicRepository;
import UserService.userService.repository.UserRepository;
import UserService.userService.request.MediaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<MediaResponse> getLikedMedia(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        List<Long> likedMediaIds = user.getLikedMedia();

        return likedMediaIds.stream()
                .map(mediaId -> {
                    Music music = musicRepository.findById(mediaId)
                            .orElseThrow(() -> new MediaNotFoundException("Media with ID " + mediaId + " not found."));
                    return new MediaResponse(music.getId(), music.getTitle());
                })
                .collect(Collectors.toList());
    }
}
