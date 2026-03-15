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

    @Autowired 
    BarbeariaRepository repository;

    @GetMapping
    public List<Barbearia> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public Barbearia adicionar(@RequestBody Barbearia barbearia) {
        // Garante que toda nova barbearia já entra com o sistema liberado!
        barbearia.setAtivo(true); 
        return service.criar(barbearia);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Barbearia dadosLogin) {
        Optional<Barbearia> barbeariaEncontrada = repository.findByEmailAndSenha(
                dadosLogin.getEmail(), 
                dadosLogin.getSenha()
        );

        if (barbeariaEncontrada.isPresent()) {
            Barbearia barbearia = barbeariaEncontrada.get();
            
            // --- TRAVA DE SEGURANÇA 1: BLOQUEIA O PAINEL ---
            if (barbearia.getAtivo() != null && !barbearia.getAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Assinatura inativa. Entre em contato com o suporte.");
            }
            // -----------------------------------------------

            return ResponseEntity.ok(barbearia); // Login com sucesso
        } else {
            // E-mail ou senha errados
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> buscarPorSlug(@PathVariable String slug) {
        Optional<Barbearia> barbeariaEncontrada = repository.findBySlug(slug);
        
        if (barbeariaEncontrada.isPresent()) {
            Barbearia barbearia = barbeariaEncontrada.get();
            
            // --- TRAVA DE SEGURANÇA 2: BLOQUEIA A TELA DO CLIENTE ---
            if (barbearia.getAtivo() != null && !barbearia.getAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Página temporariamente indisponível.");
            }
            // --------------------------------------------------------

            return ResponseEntity.ok(barbearia);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Barbearia não encontrada.");
        }
    }

    // Rota secreta do dono para BLOQUEAR uma barbearia
    @PostMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquearBarbearia(@PathVariable Long id) {
        Optional<Barbearia> b = repository.findById(id);
        if (b.isPresent()) {
            Barbearia barbearia = b.get();
            barbearia.setAtivo(false); // Corta o acesso!
            repository.save(barbearia);
            return ResponseEntity.ok("✂️ Barbearia " + barbearia.getNome() + " bloqueada com sucesso!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Barbearia não encontrada.");
    }

    // Rota secreta do dono para DESBLOQUEAR
    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<?> desbloquearBarbearia(@PathVariable Long id) {
        Optional<Barbearia> b = repository.findById(id);
        if (b.isPresent()) {
            Barbearia barbearia = b.get();
            barbearia.setAtivo(true); // Devolve o acesso!
            repository.save(barbearia);
            return ResponseEntity.ok("✅ Barbearia " + barbearia.getNome() + " liberada!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Barbearia não encontrada.");
    }
}