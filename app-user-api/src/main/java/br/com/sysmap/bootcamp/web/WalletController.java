package br.com.sysmap.bootcamp.web;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
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

    @Operation(summary = "Credit value in wallet")
    @PostMapping("/credit/{value}")
    public ResponseEntity<Wallet> save(@PathVariable("value") BigDecimal value) {
        return ResponseEntity.ok(this.service.credit(value));
    }

    @Operation(summary = "My Wallet")
    @GetMapping({ "", "/" })
    public Wallet get() {
        return service.getByUser();
    }
    
}