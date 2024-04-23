package br.com.sysmap.bootcamp.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.service.AlbumService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    // sale tests
    @Test
    @DisplayName("should return album when album is sale")
    public void shouldReturnAlbumWhenAlbumIsSale() throws Exception {
        Album album = Album.builder().build();
        album.setId(1L);
        album.setName("Test Album");
        album.setIdSpotify("spotifyId");
        album.setArtistName("Test Artist");
        album.setImageUrl("http://example.com/image.jpg");
        album.setValue(BigDecimal.valueOf(10.0));

        when(albumService.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"id\":1,\"name\":\"Test Album\",\"idSpotify\":\"spotifyId\",\"artistName\":\"Test Artist\",\"imageUrl\":\"http://example.com/image.jpg\",\"value\":10.0}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Album"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idSpotify").value("spotifyId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.artistName").value("Test Artist"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value("http://example.com/image.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(10.0));

        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumService, times(1)).save(albumCaptor.capture());
        Album capturedAlbum = albumCaptor.getValue();
        assert capturedAlbum != null;
        assert capturedAlbum.getName().equals("Test Album");
    }

    // get albums my collection tests
    @Test
    @DisplayName("should return albums from user's collection")
    public void shouldReturnAlbumsFromUsersCollection() throws Exception {
        when(albumService.getAlbums()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/my-collection")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        verify(albumService, times(1)).getAlbums();
    }

    // get albums tests
    @Test
    @DisplayName("should return albums from Spotify service by Text parameter")
    public void shouldReturnAlbumsFromSpotifyServiceByTextParameter() throws Exception {
        when(albumService.getAlbums(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/all?search=test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        verify(albumService, times(1)).getAlbums("test");
    }

    // delete album by id
    @Test
    @DisplayName("should delete an album by ID")
    public void shouldDeleteAnAlbumById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/albums/remove/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(albumService, times(1)).delete(1L);
    }
}
