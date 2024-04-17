package br.com.sysmap.bootcamp.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sysmap.bootcamp.domain.entities.Users;

public interface UsersRepository extends JpaRepository<Users, Long>{
}