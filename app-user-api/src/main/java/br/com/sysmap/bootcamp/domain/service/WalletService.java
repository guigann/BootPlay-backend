package br.com.sysmap.bootcamp.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.sysmap.bootcamp.domain.entities.Points;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService {

    private final BigDecimal DEFAULT_BALANCE = new BigDecimal(1000);
    private final Long DEFAULT_POINTS = 0L;

    private final WalletRepository repository;
    private final UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet create(String email) {
        Users user = usersService.findByEmail(email);
        Optional<Wallet> walletOptional = this.repository.findByUsers(user);
        if (walletOptional.isPresent()) {
            throw new ResourceAlreadyExistsException("Records already found for this user: user already have a wallet");
        }

        log.info("Creating wallet: {}");
        return this.repository.save(
                Wallet.builder()
                        .balance(DEFAULT_BALANCE)
                        .points(DEFAULT_POINTS)
                        .lastUpdate(LocalDateTime.now())
                        .users(user)
                        .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet credit(BigDecimal value) {
        Users user = getUser();

        Wallet wallet = getWallet(user);

        wallet.setBalance(wallet.getBalance().add(value));
        wallet.setLastUpdate(LocalDateTime.now());

        return repository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void debit(WalletDto walletDto) {
        Users user = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = getWallet(user);

        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

        LocalDateTime date = LocalDateTime.now();
        wallet.setPoints(Long.sum(wallet.getPoints(), Points.getPoints(date.getDayOfWeek())));
        wallet.setLastUpdate(date);
        repository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet getByUser() {
        Users user = getUser();

        log.info("Getting wallet: {}");
        return getWallet(user);
    }

    private Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getPrincipal().toString();
            return usersService.findByEmail(username);
        } else {
            throw new ResourceNotFoundException("No records found: there is no authenticated user");
        }
    }

    private Wallet getWallet(Users user) {
        return this.repository.findByUsers(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No records found for this user: user does not have a wallet"));
    }

}