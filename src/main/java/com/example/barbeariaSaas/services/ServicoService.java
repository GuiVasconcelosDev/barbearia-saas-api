package com.example.barbeariaSaas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.barbeariaSaas.models.Servico;
import com.example.barbeariaSaas.repository.ServicoRepository;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository repository;

    public List<Servico> listarPorBarbearia(Long barbeariaId) {
        return repository.findByBarbeariaId(barbeariaId);
    }

    public Servico criar(Servico servico) {
        return repository.save(servico);
    }
}
