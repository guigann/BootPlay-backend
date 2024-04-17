package br.com.sysmap.bootcamp.domain.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    public final UsersService service;

    @PostMapping({ "", "/" })
    public ResponseEntity<Users> save(@RequestBody Users user) {
        return ResponseEntity.ok(this.service.save(user));
    }

    @PutMapping({ "", "/" })
    public ResponseEntity<Users> update(@RequestBody Users user) {
        return ResponseEntity.ok(this.service.update(user));
    }

    @GetMapping({ "", "/" })
    public List<Users> get() {
        return service.get();
    }

    @GetMapping("/{id}")
    public Users get(@PathVariable("id") Long id) {
        return service.getById(id);
    }
}