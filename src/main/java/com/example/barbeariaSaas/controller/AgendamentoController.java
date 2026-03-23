package com.example.barbeariaSaas.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @PostMapping("/{id}/faltou")
    public ResponseEntity<?> marcarFalta(@PathVariable Long id) {
        Optional<Agendamento> ag = repository.findById(id);
        if (ag.isPresent()) {
            Agendamento agendamento = ag.get();
            agendamento.setFaltou(true); // Marca que o cliente deu o cano!
            repository.save(agendamento);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/barbearia/{id}/livres")
    public ResponseEntity<List<String>> buscarHorariosLivresHoje(@PathVariable Long id) {

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX);

        List<Agendamento> ocupados = repository.findByBarbeariaIdAndDataHoraInicioBetweenConluidoFalseAndFaltouFalse(id, inicioDoDia, fimDoDia);

        List<String> todosHorarios = Arrays.asList(
            "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        );

        List<String> horasOcupadas = ocupados.stream()
            .map(ag -> ag.getDataHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")))
            .collect(Collectors.toList());

        List<String> horariosLivres = todosHorarios.stream()
                .filter(horario -> !horasOcupadas.contains(horario))
                .collect(Collectors.toList());

        // Devolve a lista limpa para o Gemini ler! (Ex: ["09:00", "10:00"])
        return ResponseEntity.ok(horariosLivres);
    }
}
