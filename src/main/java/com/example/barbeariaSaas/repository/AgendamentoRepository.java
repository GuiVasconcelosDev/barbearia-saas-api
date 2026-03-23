package com.example.barbeariaSaas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.barbeariaSaas.models.Agendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Busca todos os agendamentos de uma barbearia
    List<Agendamento> findByBarbeariaId(Long barbeariaId);

    // Busca a agenda de um barbeiro específico
    List<Agendamento> findByBarbeiroId(Long barbeiroId);

    // A MÁGICA: Query para checar se o horário está livre para aquele barbeiro
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId " +
           "AND a.status != 'CANCELADO' " +
           "AND (a.dataHoraInicio < :fim AND a.dataHoraFim > :inicio)")
    boolean existeConflitoDeHorario(
            @Param("barbeiroId") Long barbeiroId, 
            @Param("inicio") LocalDateTime inicio, 
            @Param("fim") LocalDateTime fim
    );

    // O Radar do Robô do WhatsApp
    List<Agendamento> findByDataHoraInicioBetweenAndLembreteEnviadoFalseAndConcluidoFalseAndFaltouFalse(
            LocalDateTime inicio, 
            LocalDateTime fim
    );

    List<Agendamento> findByBarbeariaIdAndDataHoraInicioBetweenConluidoFalseAndFaltouFalse(Long id,
            LocalDateTime inicioDoDia, LocalDateTime fimDoDia);
}
