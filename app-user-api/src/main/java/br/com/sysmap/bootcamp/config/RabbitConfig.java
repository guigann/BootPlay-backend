package br.com.sysmap.bootcamp.config;

import br.com.sysmap.bootcamp.domain.listeners.WalletListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RabbitConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Queue WalletQueue() {
        return new Queue("WalletQueue");
    }

    @Bean
    public WalletListener receiver() {
        return new WalletListener();
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("br.com.sysmap.bootcamp.dto.*", "java.util.*"));
        return converter;
    }

}