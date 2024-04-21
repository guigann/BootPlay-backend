package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

 
    // loadByUsername tests
    @Test
    @DisplayName("Should load user by username and return UserDetails")
    public void shouldLoadUserByUsernameAndReturnUserDetails() {
        Users user = Users.builder().id(1L).email("user1@example.com").password("password1").build();
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
        Users user = Users.builder().id(1L).email("user1@example.com").password("password1").build();
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
