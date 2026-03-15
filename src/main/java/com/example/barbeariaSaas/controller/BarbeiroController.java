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

import com.example.barbeariaSaas.models.Barbeiro;
import com.example.barbeariaSaas.services.BarbeiroService;

@RestController
@RequestMapping("/api/barbeiros")
@CrossOrigin(origins = "*")
public class BarbeiroController {


    @Autowired
    private BarbeiroService service;

    @GetMapping("/barbearia/{barbeariaId}")
    public List<Barbeiro> listarPorBarbearia(@PathVariable Long barbeariaId) {
        return service.listarPorBarbearia(barbeariaId);
    }

    @PostMapping
    public Barbeiro adicionar(@RequestBody Barbeiro barbeiro) {
        return service.criar(barbeiro);
    }
}
