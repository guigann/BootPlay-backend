package br.com.sysmap.bootcamp.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService {

    private final UsersRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user) {
        log.info("Saving user: {}", user);
        return this.repository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users update(Users user) {
        log.info("Updating user: {}", user);

        var entity = repository.findById(user.getId()).orElseThrow(()-> new ResourceNotFoundException("No records found for this ID"));
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());

        return this.repository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Users> get() {
        log.info("Getting users: {}");
        return this.repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users getById(Long id) {
        log.info("Getting user: {}");

        return this.repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("No records found for this ID"));
    }

}