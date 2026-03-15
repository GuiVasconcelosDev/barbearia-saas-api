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

import com.example.barbeariaSaas.models.Agendamento;
import com.example.barbeariaSaas.repository.AgendamentoRepository;
import com.example.barbeariaSaas.services.AgendamentoService;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;

    @Autowired
    private AgendamentoRepository repository;

    @GetMapping("/barbearia/{barbeariaId}")
    public List<Agendamento> listarPorBarbearia(@PathVariable Long barbeariaId) {
        return service.listarPorBarbearia(barbeariaId);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) {
        try {
            Agendamento novoAgendamento = service.realizarAgendamento(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAgendamento);
        } catch (RuntimeException e) {
            // Se cair aqui, é porque deu conflito de horário ou o serviço não existe!
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> concluirAgendamento(@PathVariable Long id) {
        Optional<Agendamento> ag = repository.findById(id);
        if (ag.isPresent()) {
            Agendamento agendamento = ag.get();
            agendamento.setConcluido(true); // Guarda no cofre!
            repository.save(agendamento);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
