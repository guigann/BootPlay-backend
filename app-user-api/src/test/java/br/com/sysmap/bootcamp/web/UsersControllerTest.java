package br.com.sysmap.bootcamp.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Autowired
    private PasswordEncoder encoder;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    // create tests
    // @Test
    // @DisplayName("Should return users when valid users is created")
    // public void shouldReturnUsersWhenValidUsersIsCreated() throws Exception {
    //     Users user = Users.builder().id(1L).name("test").email("test").password("test").build();
    //     Users savedUser = Users.builder().id(1L).name("test").email("test").password(encoder.encode("test")).build();

    //     Mockito.when(usersService.create(user)).thenReturn(savedUser);

    //     mockMvc.perform(post("/users/create")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(user)))
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.id").value(savedUser.getId()))
    //             .andExpect(jsonPath("$.name").value(savedUser.getName()))
    //             .andExpect(jsonPath("$.email").value(savedUser.getEmail()));

    //     assertTrue(encoder.matches("test", savedUser.getPassword()));
    // }

    // @Test
    // @DisplayName("Should throw InvalidFieldException when creating user with missing or invalid fields")
    // public void shouldThrowInvalidFieldExceptionWhenCreatingUserWithMissingOrInvalidFields() throws Exception {
    //     Users invalidUser = Users.builder().build();

    //     mockMvc.perform(post("/users/create")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidUser)))
    //             .andExpect(status().isBadRequest());
    // }

    // update tests

    // list users tests
    @Test
    @DisplayName("Should return list of users")
    public void shouldReturnListOfUsers() throws Exception {
        Users user1 = Users.builder().id(1L).name("John").email("john@example.com").password("password1").build();
        Users user2 = Users.builder().id(2L).name("Alice").email("alice@example.com").password("password2").build();
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
        Users user = Users.builder().id(1L).name("John").email("john@example.com").password("password").build();

        Mockito.when(usersService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    // auth tests

}
