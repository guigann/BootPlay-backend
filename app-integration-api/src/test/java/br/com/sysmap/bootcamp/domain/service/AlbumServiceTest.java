package br.com.sysmap.bootcamp.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.rabbitmq.client.AMQP.Queue;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AlbumServiceTest {

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private UsersService usersService;

    @MockBean
    private Queue queue;

    @MockBean
    private RabbitTemplate template;

    @MockBean
    private SpotifyApi spotifyApi;

    @MockBean
    private AlbumModel albumModel;

    // save tests

    // get albums tests

    // get albums by search tests

    // get user tests
    @Test
    @DisplayName("Should return user when getUser() is called")
    public void shouldReturnUserWhenGetUserCalled() {
        String username = "test@example.com";
        Users expectedUser = Users.builder().email(username).build();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));
        when(albumService.getUser()).thenReturn(expectedUser);

        Users retrievedUser = albumService.getUser();

        assertNotNull(retrievedUser);
        assertEquals(expectedUser, retrievedUser);
    }

    // delete by id tests

}
