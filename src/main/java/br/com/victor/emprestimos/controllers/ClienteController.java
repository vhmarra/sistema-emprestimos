package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private final ClienteService clienteService;
    private final EmprestimoService emprestimoService;

    public ClienteController(ClienteService clienteService, EmprestimoService emprestimoService) {
        this.clienteService = clienteService;
        this.emprestimoService = emprestimoService;
    }

    @PostMapping("cadastra")
    public ResponseEntity<?> cadastraCliente(@ModelAttribute CadastraClienteRequest request){
        clienteService.cadastraCliente(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("autentica")
    public ResponseEntity<?> autentica(@RequestHeader String cpf, @RequestHeader String senha){
        clienteService.autentica(cpf,senha);
        return ResponseEntity.ok().build();
    }

    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token, @ModelAttribute EmprestimoRequest request){
        emprestimoService.solicitaEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }


}
