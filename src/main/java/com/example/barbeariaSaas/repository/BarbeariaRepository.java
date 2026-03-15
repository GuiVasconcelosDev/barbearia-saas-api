package com.example.barbeariaSaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.barbeariaSaas.models.Barbearia;

@Repository
public interface BarbeariaRepository extends JpaRepository<Barbearia, Long> {

    Optional<Barbearia> findByEmailAndSenha(String email, String senha);
    Optional<Barbearia> findBySlug(String slug);
}
