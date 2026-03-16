package com.example.barbeariaSaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.barbeariaSaas.models.BarbeiroServico;

public interface BarbeiroServicoRepository extends JpaRepository<BarbeiroServico, Long> {

    // Busca todos os serviços que um barbeiro específico faz
    List<BarbeiroServico> findByBarbeiroId(Long barbeiroId);
    
    // Busca todos os barbeiros que fazem um serviço específico
    List<BarbeiroServico> findByServicoId(Long servicoId);
}
