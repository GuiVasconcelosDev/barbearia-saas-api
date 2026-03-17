package com.example.barbeariaSaas.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.barbeariaSaas.models.Agendamento;
import com.example.barbeariaSaas.repository.AgendamentoRepository;

@Component
public class LembreteSheduler {

    @Autowired
    private AgendamentoRepository repository;

    // Roda automaticamente a cada 15 minutos (900000 milissegundos)
    @Scheduled(fixedRate = 900000)
    public void buscarAgendamentosParaLembrar() {
        // Que horas são agora?
        LocalDateTime agora = LocalDateTime.now();
        // Qual é a hora limite do radar? (Daqui a 2 horas)
        LocalDateTime daquiA2Horas = agora.plusHours(2);

        // Dispara o Radar!
        List<Agendamento> proximos = repository.findByDataHoraInicioBetweenAndLembreteEnviadoFalseAndConcluidoFalseAndFaltouFalse(agora, daquiA2Horas);

        if (!proximos.isEmpty()) {
            System.out.println("🤖 Robô acordou! Encontrou " + proximos.size() + " clientes para lembrar.");
        }

        for (Agendamento ag : proximos) {
            String telefone = ag.getCliente().getTelefone();
            String nomeCliente = ag.getCliente().getNome();
            String barbeiro = ag.getBarbeiro().getNome();

            // AQUI É ONDE VAMOS CONECTAR O NODE.JS NA PRÓXIMA ETAPA!
            System.out.println("📲 Preparando envio para: " + nomeCliente + " no número " + telefone);
            System.out.println("Mensagem: Fala " + nomeCliente + ", passando pra lembrar do seu corte com o " + barbeiro + " em breve!");

            // Marca que já enviou para não mandar de novo daqui a 15 minutos
            ag.setLembreteEnviado(true);
            repository.save(ag);
        }
    }
}
