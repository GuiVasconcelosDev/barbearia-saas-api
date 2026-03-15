package com.example.barbeariaSaas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.barbeariaSaas.models.Barbearia;
import com.example.barbeariaSaas.repository.BarbeariaRepository;
import com.example.barbeariaSaas.services.BarbeariaService;

@RestController
@RequestMapping("/api/barbearias")
@CrossOrigin(origins = "*")
public class BarbeariaController {

    @Autowired
    private BarbeariaService service;

    @Autowired BarbeariaRepository repository;

    @GetMapping
    public List<Barbearia> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public Barbearia adicionar(@RequestBody Barbearia barbearia) {
        return service.criar(barbearia);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Barbearia dadosLogin) {
        Optional<Barbearia> barbeariaEncontrada = repository.findByEmailAndSenha(
                dadosLogin.getEmail(), 
                dadosLogin.getSenha()
        );

        if (barbeariaEncontrada.isPresent()) {
            return ResponseEntity.ok(barbeariaEncontrada.get()); // Login com sucesso
        } else {
            // E-mail ou senha errados
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }

    @GetMapping("/slug/{slug}")

    public ResponseEntity<?> buscarPorSlug(@PathVariable String slug) {
        Optional<Barbearia> barbearia = repository.findBySlug(slug);
        if (barbearia.isPresent()) {
            return ResponseEntity.ok(barbearia.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Barbearia não encontrada.");
        }
    }
}
