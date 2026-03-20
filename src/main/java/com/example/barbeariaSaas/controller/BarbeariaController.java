package com.example.barbeariaSaas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

// Importação da ferramenta de Criptografia
import org.mindrot.jbcrypt.BCrypt;

import com.example.barbeariaSaas.models.Barbearia;
import com.example.barbeariaSaas.repository.BarbeariaRepository;
import com.example.barbeariaSaas.services.BarbeariaService;
import com.example.barbeariaSaas.services.TokenService;

@RestController
@RequestMapping("/api/barbearias")
@CrossOrigin(origins = "*")
public class BarbeariaController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BarbeariaService service;

    @Autowired 
    BarbeariaRepository repository;

    @GetMapping
    public List<Barbearia> listarTodas() {
        return service.listarTodas();
    }

   @PostMapping
    public ResponseEntity<?> adicionar(@RequestBody Barbearia barbearia) {
        if (repository.findByEmail(barbearia.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Este e-mail já está cadastrado.");
        }
        
        if (repository.findBySlug(barbearia.getSlug()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Este link já está em uso. Escolha outro nome.");
        }

        barbearia.setAtivo(true); 
        Barbearia novaBarbearia = service.criar(barbearia);
        return ResponseEntity.ok(novaBarbearia);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Barbearia dadosLogin) {
        // 1. Agora o banco procura APENAS pelo e-mail
        Optional<Barbearia> barbeariaOpt = repository.findByEmail(dadosLogin.getEmail());

        if (barbeariaOpt.isPresent()) {
            Barbearia barbearia = barbeariaOpt.get();
            
            // 2. O BCrypt verifica se a senha digitada bate com o Hash do banco
            boolean senhaCorreta = BCrypt.checkpw(dadosLogin.getSenha(), barbearia.getSenha());
            
            if (senhaCorreta) {
                // --- TRAVA DE SEGURANÇA 1: BLOQUEIA O PAINEL ---
                if (barbearia.getAtivo() != null && !barbearia.getAtivo()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Assinatura inativa. Entre em contato com o suporte.");
                }
                // -----------------------------------------------

                // 3. A MÁGICA: Gera o Token JWT (O Crachá)
                String token = tokenService.gerarToken(barbearia);

                // 4. Prepara a resposta enviando os dados da barbearia E o token juntos
                Map<String, Object> resposta = new HashMap<>();
                resposta.put("barbearia", barbearia);
                resposta.put("token", token);

                return ResponseEntity.ok(resposta); // Login com sucesso + Crachá entregue!
            } else {
                // Senha errada
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
            }
        } else {
            // E-mail não encontrado
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