package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.services.AlteraDadosService;
import br.com.victor.emprestimos.services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("auth")
public class AutenticaController {

    private final ClienteService clienteService;
    private final AlteraDadosService alteraDadosService;

    public AutenticaController(ClienteService clienteService, AlteraDadosService alteraDadosService) {
        this.clienteService = clienteService;
        this.alteraDadosService = alteraDadosService;
    }

    @PostMapping("cadastra")
    public ResponseEntity<?> cadastraCliente(@ModelAttribute @RequestAttribute CadastraClienteRequest request) throws InvalidInputException, MessagingException {
        clienteService.cadastraCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("autentica")
    public ResponseEntity<?> autentica(@RequestHeader String cpf, @RequestHeader String senha) throws InvalidCredencialsException, InvalidInputException {
        return ResponseEntity.ok(clienteService.autentica(cpf,senha));
    }

    @PostMapping("solicita-troca-senha")
    public ResponseEntity<?> solicitaTrocaSenha(@RequestHeader String cpf) throws Exception {
        alteraDadosService.solicitaAlteraSenha(cpf);
        return ResponseEntity.ok().build();
    }

    @PostMapping("troca-senha")
    public ResponseEntity<?> trocaSenha(@RequestHeader String token, @RequestHeader String novaSenha) throws Exception{
        alteraDadosService.alteraSenha(token,novaSenha);
        return ResponseEntity.ok().build();
    }

}
