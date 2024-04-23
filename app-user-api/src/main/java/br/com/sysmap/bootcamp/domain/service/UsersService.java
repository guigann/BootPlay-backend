package br.com.sysmap.bootcamp.domain.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.InvalidFieldException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository repository;
    private final PasswordEncoder encoder;
    private final RabbitTemplate rabbitTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users create(Users user) {
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();

        if (name == null || name.isEmpty() || name.isBlank() ||
                email == null || email.isEmpty() || email.isBlank() ||
                password == null || password.isEmpty() || password.isBlank()) {
            throw new InvalidFieldException("User with invalid fields: null, empty or blank");
        }

        Optional<Users> usersOptional = this.repository.findByEmail(user.getEmail());
        if (usersOptional.isPresent()) {
            throw new ResourceAlreadyExistsException("Records already found for this email: user with this email already exists");
        }

        user = user.toBuilder().password(this.encoder.encode(user.getPassword())).build();

        log.info("Creating user: {}", user);
        Users savedUser = this.repository.save(user);
        rabbitTemplate.convertAndSend("WalletQueue", user.getEmail());

        return savedUser;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users update(Users user) {
        log.info("Updating user: {}", user);

        var entity = repository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: user does not exists"));

        return this.repository.save(Users.builder()
                .id(entity.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(this.encoder.encode(user.getPassword()))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Users> get() {
        log.info("Getting users: {}");
        return this.repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users getById(Long id) {
        log.info("Getting user: {}");

        return this.repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: user does not exists"));
    }

    public AuthDto auth(AuthDto authDto) {
        Users user = this.findByEmail(authDto.getEmail());

        if (!this.encoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(user.getEmail()).append(":").append(user.getPassword());

        return AuthDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .token(Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(password
                                .toString()
                                .getBytes()))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = email
        Optional<Users> usersOptional = this.repository.findByEmail(username);

        return usersOptional
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found: user does not exists"));
    }
}