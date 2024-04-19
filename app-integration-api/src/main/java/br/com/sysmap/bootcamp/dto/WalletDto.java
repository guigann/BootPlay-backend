package br.com.sysmap.bootcamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class WalletDto implements Serializable {

    private String email;
    private BigDecimal value;

    public WalletDto(String email, BigDecimal value) {
        this.email = email;
        this.value = value;
    }

}