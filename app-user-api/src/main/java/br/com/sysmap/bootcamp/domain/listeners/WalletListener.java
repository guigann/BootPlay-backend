package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "WalletQueue")
public class WalletListener {

    private final WalletService walletService;
    // provisório, essa inejeção de dependencias não poderia estar aqui
    private final UsersService usersService;

    @RabbitHandler
    public void receive(WalletDto walletDto) {
        log.info("Debiting wallet: {}", walletDto);
        walletService.debit(walletDto);
    }

    @RabbitHandler
    public void receive(String email) {
        Users user = usersService.findByEmail(email);
        walletService.create(user);
    }
}