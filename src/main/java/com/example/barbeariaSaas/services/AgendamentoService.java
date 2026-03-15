package com.example.barbeariaSaas.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.barbeariaSaas.models.Agendamento;
import com.example.barbeariaSaas.models.Cliente;
import com.example.barbeariaSaas.models.Servico;
import com.example.barbeariaSaas.repository.AgendamentoRepository;
import com.example.barbeariaSaas.repository.ClienteRepository;
import com.example.barbeariaSaas.repository.ServicoRepository;

@Service
public class AgendamentoService {
    
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Agendamento> listarPorBarbearia(Long barbeariaId) {
        return agendamentoRepository.findByBarbeariaId(barbeariaId);
    }

    public Agendamento realizarAgendamento(Agendamento agendamento) {
        // 1. Lidar com o Cliente (Salva se for novo, ou usa o existente)
        Cliente cliente = agendamento.getCliente();
        Cliente clienteExistente = clienteRepository.findByTelefone(cliente.getTelefone()).orElse(null);
        
        if (clienteExistente != null) {
            agendamento.setCliente(clienteExistente);
        } else {
            // Salva o novo cliente no banco
            clienteRepository.save(cliente);
        }

        // 2. Buscar o Serviço para descobrir a duração
        Servico servico = servicoRepository.findById(agendamento.getServico().getId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // 3. Calcular a Data/Hora do Fim do atendimento
        LocalDateTime inicio = agendamento.getDataHoraInicio();
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
        agendamento.setDataHoraFim(fim);

        // 4. Verificar Conflitos de Horário na agenda do Barbeiro
        boolean temConflito = agendamentoRepository.existeConflitoDeHorario(
                agendamento.getBarbeiro().getId(), 
                inicio, 
                fim
        );

        if (temConflito) {
            throw new RuntimeException("O barbeiro já possui um agendamento neste horário.");
        }

        // 5. Se tudo estiver certo, salva o agendamento!
        agendamento.setStatus("CONFIRMADO");
        return agendamentoRepository.save(agendamento);
    }
}
