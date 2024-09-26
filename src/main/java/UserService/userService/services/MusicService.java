package UserService.userService.services;

import UserService.userService.repositories.MusicRepository;
import UserService.userService.vo.Genre;
import UserService.userService.vo.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MusicService {

    private MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Music> findAllMusic() {
        return musicRepository.findAll();
    }

    public boolean musicExistsByUrl(String url) {
        return musicRepository.existsByUrlIgnoreCase(url);
    }

    public Music findMusicByUrl(String url) {
        Optional<Music> optionalMusic = musicRepository.findByUrlIgnoreCase(url);

        return optionalMusic.orElse(null);
    }

    public List<Music> findAllMusicInGenre(Genre genre) {
        return musicRepository.findMusicByGenres(genre);
    }

    public Music addPlay(Music music) {
        music.countPlay();
        musicRepository.save(music);

        return music;
    }
}
