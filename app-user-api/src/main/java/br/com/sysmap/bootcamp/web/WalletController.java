package br.com.sysmap.bootcamp.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    public final WalletService service;

    // @Operation(summary = "Credit value in wallet")
    // @PostMapping("/credit/{value}")
    // public ResponseEntity<Wallet> save(@RequestBody WalletDto walletDto, @PathVariable("value") BigDecimal value) {
    //     return ResponseEntity.ok(this.service.credit(walletDto, value));
    // }

    // @Operation(summary = "My Wallet")
    // @GetMapping("/")
    // public Wallet get(@RequestBody) {
    //     return service.getById(id);
    // }
}