package com.example.barbeariaSaas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.barbeariaSaas.models.BarbeiroServico;
import com.example.barbeariaSaas.repository.BarbeiroServicoRepository;

public class BarbeiroServicoController {

    @Autowired
    private BarbeiroServicoRepository repository;

    // Cadastra um tempo novo para um barbeiro num serviço
    @PostMapping
    public BarbeiroServico configurarTempo(@RequestBody BarbeiroServico bs) {
        return repository.save(bs);
    }

    // O React vai usar isto para saber quanto tempo o barbeiro X demora
    @GetMapping("/barbeiro/{barbeiroId}")
    public List<BarbeiroServico> buscarPorBarbeiro(@PathVariable Long barbeiroId) {
        return repository.findByBarbeiroId(barbeiroId);
    }
}
