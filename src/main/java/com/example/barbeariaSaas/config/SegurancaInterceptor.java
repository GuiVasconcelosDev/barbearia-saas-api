package com.example.barbeariaSaas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.barbeariaSaas.services.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SegurancaInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String metodo = request.getMethod();

        // 1. ROTAS PÚBLICAS (Deixa passar sem crachá)
        // Login, Tela do Cliente (slug) e requisições do navegador (OPTIONS)
        if (uri.contains("/login") || uri.contains("/slug/") || metodo.equals("OPTIONS")) {
            return true;
        }

        // Criar conta de barbearia também tem de ser público
        if (uri.equals("/api/barbearias") && metodo.equals("POST")) {
            return true;
        }

        // 2. ROTAS PRIVADAS (Exige o crachá!)
        String tokenHeader = request.getHeader("Authorization");
        
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7); // Tira a palavra "Bearer " e pega só o código
            String emailValido = tokenService.validarToken(token);

            if (!emailValido.isEmpty()) {
                return true; // Crachá válido! Pode entrar.
            }
        }

        // 3. SEM CRACHÁ OU CRACHÁ FALSO: Expulsa!
        response.setStatus(401);
        response.getWriter().write("Acesso Negado: Faca login novamente.");
        return false;
    }
}