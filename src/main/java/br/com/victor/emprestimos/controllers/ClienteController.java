package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.dtos.ClienteAlteraEmprestimoRequest;
import br.com.victor.emprestimos.dtos.EmprestimoDto;
import br.com.victor.emprestimos.dtos.EmprestimoRequest;
import br.com.victor.emprestimos.dtos.LoginClientRequest;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import br.com.victor.emprestimos.services.TokenService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Transactional;
import java.util.List;

@RequestMapping("cliente")
@RestController
public class ClienteController {

    private ClienteService service;
    private AuthenticationManager authManager;
    private TokenService tokenService;
    private EmprestimoService emprestimoService;

    public ClienteController(ClienteService service, AuthenticationManager authManager, TokenService tokenService, EmprestimoService emprestimoService) {
        this.service = service;
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "valor",defaultValue = "teste") String valor){
        return ResponseEntity.ok(String.format("Hello %s",valor)).getBody();
    }

    @Transactional
    @PostMapping("cadastro")
    public ResponseEntity<?> cadastraCliente(@ModelAttribute CadastraClienteRequest request) throws Exception {
        service.cadastraCliente(request);
        return ResponseEntity.status(201).build();
    }

    @Transactional
    @PostMapping("login")
    public ResponseEntity<?> loginCliente(@RequestBody LoginClientRequest request) throws Exception {
        try{
            service.loginCliente(request);
            return ResponseEntity.ok().build();
        }
        catch (InvalidTransactionException e){
            throw new Exception("ERRO AO LOGAR");
        }

    }

    @Transactional
    @PostMapping("solicita-emprestimo")
    public ResponseEntity<?> solicitaEmprestimo(@RequestHeader String token,@RequestBody EmprestimoRequest request) throws AuthenticationException, InvalidTokenException {
        emprestimoService.solicitaEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @GetMapping("listar-emprestimos")
    public ResponseEntity<List<EmprestimoDto>> listaEmprestimos(@RequestHeader String token) throws AuthenticationException, InvalidTokenException {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos(token));
    }

    @PostMapping("solicita-alteracao-status")
    public ResponseEntity<?> alteraEmprestimo(@RequestHeader String token,@ModelAttribute ClienteAlteraEmprestimoRequest request) throws InvalidTokenException {
        service.alteraEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

}
