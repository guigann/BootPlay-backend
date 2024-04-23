package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository repository;
    private final UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Album save(Album album) throws ParseException, SpotifyWebApiException, IOException {
        List<Album> userAlbums = getAlbums();

        for (Album userAlbum : userAlbums) {
            if (userAlbum.getIdSpotify().equals(album.getIdSpotify())) {
                throw new ResourceAlreadyExistsException(
                        "Records already found for this ID: This user already owns this album");
            }
        }

        Users user = getUser();
        album.setUsers(user);
        WalletDto walletDto = new WalletDto(user.getEmail(), album.getValue());

        template.convertAndSend(queue.getName(), walletDto);

        log.info("Creating album: {}", album);
        return repository.save(album);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Album> getAlbums() throws IOException, ParseException, SpotifyWebApiException {
        log.info("Getting albums: {}");
        return this.repository.findAllByUsers(getUser());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        log.info("Getting albums: {}");
        return this.spotifyApi.getAlbums(search);
    }

    protected Users getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Optional<Album> albumOptional = this.repository.findById(id);
        if (!albumOptional.isPresent()) {
            throw new ResourceNotFoundException("No records found for this ID: this album does not exist");
        }

        log.info("Removing album with id: {}:", id);
        repository.deleteById(id);
    }

}