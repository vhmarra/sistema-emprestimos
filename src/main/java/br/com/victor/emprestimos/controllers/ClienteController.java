package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteDataDTO;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token, @ModelAttribute EmprestimoRequest request) throws InvalidTokenException, InvalidInputException, InvalidCredencialsException {
        emprestimoService.solicitaEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-all")
    public List<?> getAll(@RequestHeader String token) throws InvalidCredencialsException {
        return clienteService.findAll(token);
    }

    @GetMapping("get-data")
    public ClienteDataDTO getDataIfSuper(@RequestHeader String tokenCliente, @RequestHeader String tokenAdmin) throws InvalidCredencialsException {
        return clienteService.getDataIfSuperAdmin(tokenAdmin,tokenCliente);
    }

    @PostMapping("update-emprestimo")
    public ResponseEntity<?> updateEmprestimo(@RequestHeader String token, @RequestHeader Long id, @RequestHeader StatusEmprestimo status) throws InvalidInputException, InvalidCredencialsException, InvalidTokenException {
        emprestimoService.updateEmprestimo(token,id,status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("all-by-cliente-token")
    public ResponseEntity<List<EmprestimoDto>> getAllByClienteToken(@RequestHeader String token) throws InvalidCredencialsException {
        return ResponseEntity.ok(emprestimoService.getAllByToken(token));
    }

}
