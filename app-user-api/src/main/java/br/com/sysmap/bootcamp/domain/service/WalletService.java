package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Points;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService {

    private final BigDecimal DEFAULT_BALANCE = new BigDecimal(1000);
    private final Long DEFAULT_POINTS = 0L;

    private final WalletRepository repository;
    private final UsersService usersService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet create(Users user) {
        // Talvez nao seja necessaria essa validacao, pois o usuario ainda nao foi
        // criado, e sua existencia ja havia sido validada
        Optional<Wallet> walletOptional = this.repository.findByUsers(user);
        if (walletOptional.isPresent()) {
            throw new RuntimeException("User already have a wallet");
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
        // tentar reutilizar essa declaração
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Users user = usersService.findByEmail(userEmail);

        Wallet wallet = repository.findByUsers(user)
                .orElseThrow(() -> new ResourceNotFoundException("No records for this user"));

        wallet.setBalance(wallet.getBalance().add(value));
        wallet.setLastUpdate(LocalDateTime.now());

        return repository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void debit(WalletDto walletDto) {
        Users user = usersService.findByEmail(walletDto.getEmail());

        Wallet wallet = repository.findByUsers(user)
                .orElseThrow(() -> new ResourceNotFoundException("No records for this user"));

        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

        LocalDateTime date = LocalDateTime.now();
        wallet.setPoints(Long.sum(wallet.getPoints(), Points.getPoints(date.getDayOfWeek())));
        wallet.setLastUpdate(date);

        repository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet getByUser() {
        // tentar reutilizar essa declaração
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Users user = usersService.findByEmail(userEmail);

        log.info("Getting wallet: {}");
        return this.repository.findByUsers(user)
                .orElseThrow(() -> new ResourceNotFoundException("No records for this user"));
    }

}