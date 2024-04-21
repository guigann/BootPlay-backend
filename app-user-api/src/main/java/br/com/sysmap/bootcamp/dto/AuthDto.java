package br.com.sysmap.bootcamp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthDto {

    private Long id;
    private String email;
    private String password;
    private String token;

    public AuthDto(Long id, String email, String password, String token) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public AuthDto() {
    }

}