package com.example.barbeariaSaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.barbeariaSaas.models.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByBarbeariaId(Long barbeariaId);
}
