package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Points;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private WalletService walletService;

    private final String VALID_EMAIL = "user@example.com";

    // create tests
    @Test
    @DisplayName("Should create Wallet when valid user email is provided")
    public void shouldCreateWalletWhenValidUserEmailIsProvided() {
        Users user = Users.builder().id(1L).email(VALID_EMAIL).build();
        when(usersService.findByEmail(VALID_EMAIL)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet createdWallet = walletService.create(VALID_EMAIL);

        assertNotNull(createdWallet);
        assertEquals(1L, createdWallet.getUsers().getId());
        assertEquals(BigDecimal.valueOf(1000), createdWallet.getBalance());
        assertEquals(0L, createdWallet.getPoints());
        assertNotNull(createdWallet.getLastUpdate());
        verify(walletRepository, times(1)).findByUsers(user);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when user already has a wallet")
    public void shouldThrowExceptionWhenUserAlreadyHasWallet() {
        Users user = Users.builder().id(1L).email(VALID_EMAIL).build();
        when(usersService.findByEmail(VALID_EMAIL)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(new Wallet()));

        assertThrows(ResourceAlreadyExistsException.class, () -> walletService.create(VALID_EMAIL));
        verify(walletRepository, times(1)).findByUsers(user);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    // credit tests
    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not authenticated")
    public void shouldThrowExceptionWhenUserIsNotAuthenticated() {
        BigDecimal valueToCredit = BigDecimal.valueOf(200);
        when(usersService.findByEmail(anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> walletService.credit(valueToCredit));
        verify(usersService, never()).findByEmail(anyString());
        verify(walletRepository, never()).findByUsers(any(Users.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    // debit tests
    @Test
    @DisplayName("Should debit value from Wallet balance")
    public void shouldDebitValueFromWalletBalance() {
        Users user = Users.builder().id(1L).email(VALID_EMAIL).build();
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(500))
                .points(0L)
                .lastUpdate(LocalDateTime.now())
                .users(user)
                .build();
        WalletDto walletDto = new WalletDto(VALID_EMAIL, BigDecimal.valueOf(200));
        when(usersService.findByEmail(VALID_EMAIL)).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        walletService.debit(walletDto);

        assertEquals(BigDecimal.valueOf(300), wallet.getBalance());
        assertEquals(Points.getPoints(LocalDateTime.now().getDayOfWeek()), wallet.getPoints());
        assertNotNull(wallet.getLastUpdate());
        verify(usersService, times(1)).findByEmail(VALID_EMAIL);
        verify(walletRepository, times(1)).findByUsers(user);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not have a Wallet")
    public void shouldThrowExceptionWhenUserDoesNotHaveWallet() {
        WalletDto walletDto = new WalletDto(VALID_EMAIL, BigDecimal.valueOf(200));
        when(usersService.findByEmail(VALID_EMAIL)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> walletService.debit(walletDto));
        verify(usersService, times(1)).findByEmail(VALID_EMAIL);
        verify(walletRepository, never()).findByUsers(any(Users.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

}
