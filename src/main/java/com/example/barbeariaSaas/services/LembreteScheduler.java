package com.example.barbeariaSaas.services;

import com.example.barbeariaSaas.models.Agendamento;
import com.example.barbeariaSaas.repository.AgendamentoRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LembreteScheduler {

    @Autowired
    private AgendamentoRepository repository;

    // URL do seu robô Node.js
    private final String URL_ROBO = "https://barbearia-bot-whatsapp-production.up.railway.app/api/enviar";

    // Roda automaticamente a cada 15 minutos (900000 milissegundos)
    @Scheduled(fixedRate = 900000)
    public void buscarAgendamentosParaLembrar() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime daquiA2Horas = agora.plusHours(2);

        List<Agendamento> proximos = repository.findByDataHoraInicioBetweenAndLembreteEnviadoFalseAndConcluidoFalseAndFaltouFalse(agora, daquiA2Horas);

        if (!proximos.isEmpty()) {
            System.out.println("🤖 O Java achou " + proximos.size() + " clientes para lembrar!");
            RestTemplate restTemplate = new RestTemplate();

            for (Agendamento ag : proximos) {
                // 1. Limpa o telefone (tira traços e espaços) e garante o 55 do Brasil
                String telefonePuro = ag.getCliente().getTelefone().replaceAll("\\D", "");
                if (!telefonePuro.startsWith("55")) {
                    telefonePuro = "55" + telefonePuro;
                }

                // 2. Pega a hora bonitinha (Ex: 14:00)
                String horaFormatada = ag.getDataHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"));

                // 3. Monta o texto persuasivo
                String mensagem = "Fala " + ag.getCliente().getNome() + "! Passando pra lembrar do seu corte com " + ag.getBarbeiro().getNome() + " hoje às " + horaFormatada + "h. Te esperamos lá! ✂️";

                try {
                    // 4. Prepara o pacote JSON para o Node.js
                    Map<String, String> request = new HashMap<>();
                    request.put("telefone", telefonePuro);
                    request.put("mensagem", mensagem);

                    // 5. ATIRA! (Faz o POST para o Robô)
                    restTemplate.postForEntity(URL_ROBO, request, String.class);
                    System.out.println("✅ Ordem enviada com sucesso para o robô atirar em: " + telefonePuro);

                    // 6. Trava para não mandar de novo daqui a 15 minutos
                    ag.setLembreteEnviado(true);
                    repository.save(ag);

                } catch (Exception e) {
                    System.out.println("❌ O Java não conseguiu falar com o Node: " + e.getMessage());
                }
            }
        }
    }
}
