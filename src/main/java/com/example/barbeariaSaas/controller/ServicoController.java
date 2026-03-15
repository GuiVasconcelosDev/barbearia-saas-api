package com.example.barbeariaSaas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbeariaSaas.models.Servico;
import com.example.barbeariaSaas.services.ServicoService;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    @Autowired
    private ServicoService service;

    @GetMapping("/barbearia/{barbeariaId}")
    public List<Servico> listarPorBabearia(@PathVariable Long barbeariaId) {
        return service.listarPorBarbearia(barbeariaId);
    }

    @PostMapping
    public Servico adicionar(@RequestBody Servico servico) {
        return service.criar(servico);
    }
}
