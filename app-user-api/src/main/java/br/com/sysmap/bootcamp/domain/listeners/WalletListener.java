package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import java.math.BigDecimal;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "WalletQueue")
public class WalletListener {

    private final WalletService walletService;

    // A validação de saldo está com problemas, o album está sendo criado mesmo com
    // saldo insuficiente, é um problema no método save de AlbumService
    @RabbitHandler
    public void receive(WalletDto walletDto) {
        // BigDecimal value = walletDto.getValue();
        // if (walletService.isBalanceSufficient(value)) {
        log.info("Debiting wallet: {}", walletDto);
        walletService.debit(walletDto);
        // } else {
        // throw new RuntimeException("Insufficient balance to make the purchase");
        // }
    }
}