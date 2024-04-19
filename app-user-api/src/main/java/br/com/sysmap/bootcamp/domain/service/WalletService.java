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
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository repository;

    public Wallet create(Users user) {
        // Talvez nao seja necessaria essa validacao, pois o usuario ainda nao foi
        // criado, e sua existencia ja havia sido validada
        Optional<Wallet> walletOptional = this.repository.findByUsers(user);
        if (walletOptional.isPresent()) {
            throw new RuntimeException("User already have a wallet");
        }

        // Aqui deve se criar uma wallet para o user

        log.info("Creating wallet: {}");
        return this.repository.save(
                Wallet.builder()
                        .balance(BigDecimal.valueOf(1000))
                        .points(0L)
                        .lastUpdate(LocalDateTime.now())
                        .users(user)
                        .build());
    }

    // public void credit(BigDecimal value) {
    // Users user = usersService.findByEmail(username);

    // Wallet wallet = repository.findByUsers(user)
    // .orElseThrow(() -> new ResourceNotFoundException("No records for this
    // user"));

    // wallet.setBalance(wallet.getBalance().add(walletDto.getValue()));
    // wallet.setLastUpdate(LocalDateTime.now());

    // repository.save(wallet);
    // }

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

}