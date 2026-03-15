package com.example.barbeariaSaas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.barbeariaSaas.models.Barbeiro;
import com.example.barbeariaSaas.repository.BarbeiroRepository;

@Service
public class BarbeiroService {

    @Autowired
    private BarbeiroRepository repository;

    public List<Barbeiro> listarPorBarbearia(Long barbeariaId) {
        return repository.findByBarbeariaId(barbeariaId);
    }

    public Barbeiro criar(Barbeiro barbeiro) {
        return repository.save(barbeiro);
    }
}
