package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.InvalidFieldException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private PasswordEncoder encoder;

    @MockBean
    private UsersRepository usersRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .name("test")
                .email("test")
                .password("test")
                .build();
    }

    // create tests
    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Mockito.when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(usersRepository.save(any(Users.class))).thenReturn(user);

        Users createdUser = usersService.create(user);
        assertEquals(user, createdUser);
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when trying to create a user with existing email")
    public void shouldThrowResourceAlreadyExistsExceptionWhenTryingToCreateUserWithExistingEmail() {
        Mockito.when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(ResourceAlreadyExistsException.class, () -> usersService.create(user));
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when trying to create a user with invalid fields")
    public void shouldThrowInvalidFieldExceptionWhenTryingToCreateUserWithInvalidFields() {
        Users invalidUser = Users.builder().build();

        assertThrows(InvalidFieldException.class, () -> usersService.create(invalidUser));
    }

    // update tests
    @Test
    @DisplayName("Should update user when valid user is provided")
    public void shouldUpdateUserWhenValidUserIsProvided() {

        Mockito.when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        Users updatedUser = Users.builder()
                .id(1L)
                .name("updatedTest")
                .email("updated@test.com")
                .password("newPassword")
                .build();

        Mockito.when(usersRepository.save(Mockito.any(Users.class))).thenReturn(updatedUser);

        Users result = usersService.update(updatedUser);

        assertEquals(updatedUser, result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user to be updated is not found")
    public void shouldThrowResourceNotFoundExceptionWhenUserToBeUpdatedIsNotFound() {
        Mockito.when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        Users updatedUser = Users.builder()
                .id(1L)
                .name("updatedTest")
                .email("updatedTest")
                .password("updatedTest")
                .build();

        assertThrows(ResourceNotFoundException.class, () -> usersService.update(updatedUser));
    }

    // get all tests
    @Test
    @DisplayName("Should return a list of users")
    public void shouldReturnListOfUsers() {
        List<Users> userList = new ArrayList<>();
        userList.add(Users.builder().id(1L).name("User 1").email("user1@example.com").password("password1").build());
        userList.add(Users.builder().id(2L).name("User 2").email("user2@example.com").password("password2").build());
        userList.add(Users.builder().id(3L).name("User 3").email("user3@example.com").password("password3").build());

        when(usersRepository.findAll()).thenReturn(userList);

        List<Users> result = usersService.get();

        assertEquals(userList.size(), result.size());
        for (int i = 0; i < userList.size(); i++) {
            Users expectedUser = userList.get(i);
            Users actualUser = result.get(i);
            assertEquals(expectedUser.getId(), actualUser.getId());
            assertEquals(expectedUser.getName(), actualUser.getName());
            assertEquals(expectedUser.getEmail(), actualUser.getEmail());
            assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        }
    }

    @Test
    @DisplayName("Should return an empty list if no users are found")
    public void shouldReturnEmptyListIfNoUsersFound() {
        when(usersRepository.findAll()).thenReturn(new ArrayList<>());
        List<Users> result = usersService.get();

        assertEquals(0, result.size());
    }

    // get by id tests
    @Test
    @DisplayName("Should return a user when valid ID is provided")
    public void shouldReturnUserWhenValidIdIsProvided() {
        Users user = Users.builder().id(1L).name("User 1").email("user1@example.com").password("password1").build();

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        Users result = usersService.getById(1L);

        assertEquals(user, result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user with provided ID does not exist")
    public void shouldThrowResourceNotFoundExceptionWhenUserWithProvidedIdDoesNotExist() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usersService.getById(1L));
    }

    // auth tests
    @Test
    @DisplayName("Should authenticate user and return AuthDto")
    public void shouldAuthenticateUserAndReturnAuthDto() {
        AuthDto authDto = AuthDto.builder().email("user1@example.com").password("password1").build();
        Users user = Users.builder().id(1L).name("User 1").email("user1@example.com")
                .password(encoder.encode("password1")).build();

        when(usersRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        AuthDto result = usersService.auth(authDto);

        assertEquals("user1@example.com", result.getEmail());
        assertEquals(1L, result.getId());

        int expectedTokenLength = Base64.getEncoder()
                .encodeToString(("user1@example.com" + ":" + encoder.encode("password1")).getBytes()).length();

        assertEquals(expectedTokenLength, result.getToken().length());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user email not found")
    public void shouldThrowResourceNotFoundExceptionWhenUserEmailNotFound() {
        AuthDto authDto = AuthDto.builder().email("nonexistent@example.com").password("password1").build();
        when(usersRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usersService.auth(authDto));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when password is incorrect")
    public void shouldThrowResourceNotFoundExceptionWhenPasswordIsIncorrect() {
        AuthDto authDto = AuthDto.builder().email("user1@example.com").password("incorrectPassword").build();
        Users user = Users.builder().id(1L).name("User 1").email("user1@example.com")
                .password(encoder.encode("password1")).build();

        when(usersRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class, () -> usersService.auth(authDto));
    }

    // loadByUsername tests
    @Test
    @DisplayName("Should load user by username and return UserDetails")
    public void shouldLoadUserByUsernameAndReturnUserDetails() {
        Users user = Users.builder().id(1L).name("User 1").email("user1@example.com").password("password1").build();
        when(usersRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername("user1@example.com");

        assertEquals("user1@example.com", userDetails.getUsername());
        assertEquals("password1", userDetails.getPassword());

        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(usersRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usersService.loadUserByUsername("nonexistent@example.com"));
    }

    // findByEmail tests
    @Test
    @DisplayName("Should find user by email")
    public void shouldFindUserByEmail() {
        Users user = Users.builder().id(1L).name("User 1").email("user1@example.com").password("password1").build();
        when(usersRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));

        Users foundUser = usersService.findByEmail("user1@example.com");

        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not found by email")
    public void shouldThrowResourceNotFoundExceptionWhenUserIsNotFoundByEmail() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usersService.findByEmail("nonexistent@example.com"));
    }

}
