package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.LoginClientRequest;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private final ClienteService service;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public ClienteController(ClienteService service, AuthenticationManager authManager, TokenService tokenService) {
        this.service = service;
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping("cadastro")
    public ResponseEntity<?> cadastraCliente(@RequestBody CadastraClienteRequest request){
        service.cadastraCliente(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("login")
    public ResponseEntity<?> loginCliente(@RequestBody LoginClientRequest request){
        try {
            Authentication authentication = authManager.authenticate(request.convertToAuth());
            String token = tokenService.geraToken(authentication);

            return ResponseEntity.ok().build();
        }catch (AuthenticationException e){
            return ResponseEntity.status(400).build();
        }


    }


}
