package UserService.userService.services;

import UserService.userService.repositories.MusicRepository;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MusicService {

    private MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public boolean musicExistsByUrl(String url) {
        return musicRepository.existsByUrlIgnoreCase(url);
    }

    public Music findMusicByUrl(String url) {
        Optional<Music> optionalMusic = musicRepository.findByUrlIgnoreCase(url);

        return optionalMusic.orElse(null);
    }
}
