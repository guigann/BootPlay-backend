package br.com.sysmap.bootcamp.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    public final UsersService service;

    @Operation(summary = "Create a user")
    @PostMapping("/create")
    public ResponseEntity<Users> save(@RequestBody Users user) {
        // Mudar Http Status Code para CREATED
        return ResponseEntity.ok(this.service.create(user));
    }

    @Operation(summary = "Update a user")
    @PutMapping("/update")
    public ResponseEntity<Users> update(@RequestBody Users user) {
        return ResponseEntity.ok(this.service.update(user));
    }

    @Operation(summary = "List users")
    @GetMapping({ "", "/" })
    public List<Users> get() {
        return service.get();
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public Users get(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Auth user")
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto user) {
        return ResponseEntity.ok(this.service.auth(user));
    }
}