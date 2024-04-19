package br.com.sysmap.bootcamp.domain.listeners;


import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "WalletQueue")
public class WalletListener {

    @RabbitHandler
    public void receive(WalletDto walletDto) {
        log.info(walletDto.getTeste());
    }

}