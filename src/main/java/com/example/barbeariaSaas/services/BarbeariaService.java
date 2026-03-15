package com.example.barbeariaSaas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.barbeariaSaas.models.Barbearia;
import com.example.barbeariaSaas.repository.BarbeariaRepository;

@Service
public class BarbeariaService {

    @Autowired
    private BarbeariaRepository repository;

    public List<Barbearia> listarTodas() {
        return repository.findAll();
    }

    public Barbearia criar(Barbearia barbearia) {
        return repository.save(barbearia);
    }
}
