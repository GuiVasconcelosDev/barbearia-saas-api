package com.example.barbeariaSaas.services;

import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.barbeariaSaas.models.Barbearia;

@Service
public class TokenService {
    @Value("${minha.chave.jwt}")
    private String CHAVE_SECRETA;
    public String gerarToken(Barbearia barbearia) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(CHAVE_SECRETA);
            return JWT.create()
                    .withIssuer("SaaS Barbearia")
                    .withSubject(barbearia.getEmail()) // Guarda o email no crachá
                    .withClaim("id", barbearia.getId()) // Guarda o ID no crachá
                    .withExpiresAt(gerarDataExpiracao()) // Crachá vale por 2 horas
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o crachá JWT", exception);
        }
    }

    // 2. VERIFICA O CRACHÁ (Quando o utilizador tenta aceder a uma rota protegida)
    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(CHAVE_SECRETA);
            return JWT.require(algoritmo)
                    .withIssuer("SaaS Barbearia")
                    .build()
                    .verify(token)
                    .getSubject(); // Devolve o email se estiver tudo ok
        } catch (JWTVerificationException exception) {
            return ""; // Crachá falso ou expirado
        }
    }

    // Define que o crachá expira em 2 horas (segurança caso o cliente esqueça o PC aberto)
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
