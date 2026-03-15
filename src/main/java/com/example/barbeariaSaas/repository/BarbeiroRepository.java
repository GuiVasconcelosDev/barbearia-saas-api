package com.example.barbeariaSaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.barbeariaSaas.models.Barbeiro;

@Repository
public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
    List<Barbeiro> findByBarbeariaId(Long barbeariaId);
}
