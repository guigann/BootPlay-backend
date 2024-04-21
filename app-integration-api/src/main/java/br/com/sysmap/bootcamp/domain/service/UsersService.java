package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exceptions.ResourceNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userDetail = repository.findByEmail(username);

        return userDetail
                .map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public Users findByEmail(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found: user does not exists"));
    }

}