package br.com.sysmap.bootcamp.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    // create tests
    @Test
    @DisplayName("Should create a user")
    public void shouldCreateUser() throws Exception {
        Users user = Users.builder().id(1L).name("Test").email("test@example.com").password("password").build();

        when(usersService.create(any(Users.class))).thenReturn(user);

        mockMvc.perform(post("/users/create")
                .content("{ \"name\": \"Test\", \"email\": \"test@example.com\", \"password\": \"password\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    // update tests
    @Test
    @DisplayName("Should update a user")
    public void shouldUpdateUser() throws Exception {
        // Given
        Users user = Users.builder().id(1L).name("Test").email("test@example.com").password("password").build();

        when(usersService.update(any(Users.class))).thenReturn(user);

        // When
        mockMvc.perform(put("/users/update")
                .content(
                        "{ \"id\": 1, \"name\": \"Test\", \"email\": \"test@example.com\", \"password\": \"password\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").value("password"));
    }

    // list users tests
    @Test
    @DisplayName("Should return list of users")
    public void shouldReturnListOfUsers() throws Exception {
        Users user1 = Users.builder().id(1L).name("Test").email("test@example.com").password("password1").build();
        Users user2 = Users.builder().id(2L).name("Test2").email("test2@example.com").password("password2").build();
        List<Users> userList = Arrays.asList(user1, user2);

        Mockito.when(usersService.get()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userList.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(userList.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(userList.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(userList.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(userList.get(1).getName())))
                .andExpect(jsonPath("$[1].email", is(userList.get(1).getEmail())));
    }

    @Test
    @DisplayName("Should return an empty list if no users are found")
    public void shouldReturnEmptyListIfNoUsersFound() throws Exception {
        Mockito.when(usersService.get()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // get user by id tests
    @Test
    @DisplayName("Should return user by ID")
    public void shouldReturnUserById() throws Exception {
        Users user = Users.builder().id(1L).name("Test").email("test@example.com").password("password").build();

        Mockito.when(usersService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    // auth tests
    @Test
    @DisplayName("Should authenticate a user")
    public void shouldAuthenticateUser() throws Exception {
        AuthDto authDto = AuthDto.builder().id(1L).email("test@example.com").password("password").token("token")
                .build();

        when(usersService.auth(any(AuthDto.class))).thenReturn(authDto);

        mockMvc.perform(post("/users/auth")
                .content("{ \"email\": \"test@example.com\", \"password\": \"password\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.token").value("token"));
    }

}
