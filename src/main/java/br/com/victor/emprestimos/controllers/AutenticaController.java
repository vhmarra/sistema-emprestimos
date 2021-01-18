package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AutenticaController {

    private ClienteService clienteService;

    public AutenticaController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("cadastra")
    public ResponseEntity<?> cadastraCliente(@ModelAttribute @RequestAttribute CadastraClienteRequest request) throws InvalidInputException {
        clienteService.cadastraCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("autentica")
    public ResponseEntity<?> autentica(@RequestHeader String cpf, @RequestHeader String senha) throws InvalidCredencialsException, InvalidInputException {
        return ResponseEntity.ok(clienteService.autentica(cpf,senha));
    }

}
