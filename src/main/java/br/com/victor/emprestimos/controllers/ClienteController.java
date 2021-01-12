package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.exceptions.NotFoundException;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private ClienteService clienteService;
    private EmprestimoService emprestimoService;

    public ClienteController(ClienteService clienteService, EmprestimoService emprestimoService) {
        this.clienteService = clienteService;
        this.emprestimoService = emprestimoService;
    }

    @PostMapping("cadastra")
    public ResponseEntity<?> cadastraCliente(@ModelAttribute CadastraClienteRequest request){
        clienteService.cadastraCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("autentica")
    public ResponseEntity<?> autentica(@RequestHeader String cpf, @RequestHeader String senha){
        clienteService.autentica(cpf,senha);
        return ResponseEntity.ok().build();
    }

    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token, @ModelAttribute EmprestimoRequest request) throws InvalidTokenException, InvalidInputException, InvalidCredencialsException {
        emprestimoService.solicitaEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-all-emprestimos")
    public ResponseEntity<?> updateAllEmprestimos(@RequestHeader String token, @RequestHeader StatusEmprestimo status) throws InvalidCredencialsException {
        emprestimoService.updateAllEmprestimos(token,status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-all")
    public List<Cliente> getAll(@RequestHeader String token) throws InvalidCredencialsException {
        return clienteService.findAll(token);
    }

    @GetMapping("get-data")
    public ClienteDataDTO getDataIfSuper(@RequestHeader String tokenCliente, @RequestHeader String tokenAdmin) throws InvalidCredencialsException {
        return clienteService.getDataIfSuperAdmin(tokenAdmin,tokenCliente);
    }

}
