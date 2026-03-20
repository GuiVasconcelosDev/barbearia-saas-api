package com.example.barbeariaSaas.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt; 

import com.example.barbeariaSaas.models.Barbearia;
import com.example.barbeariaSaas.repository.BarbeariaRepository;

@Service
public class BarbeariaService {

    @Autowired
    private BarbeariaRepository repository;

    public List<Barbearia> listarTodas() {
        return repository.findAll();
    }

    public Barbearia criar(Barbearia barbearia) {
        // 1. Pegamos a senha que o usuário digitou (ex: "123456")
        String senhaPura = barbearia.getSenha();
        
        // 2. Transformamos em um Hash (ex: "$2a$10$vI8..")
        String senhaHasheada = BCrypt.hashpw(senhaPura, BCrypt.gensalt());
        
        // 3. Substituímos a senha original pelo Hash antes de salvar
        barbearia.setSenha(senhaHasheada);
        
        return repository.save(barbearia);
    }
}