package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.dtos.AlteraEmprestimoRequest;
import br.com.victor.emprestimos.enums.StatusEmprestimo;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.services.ClienteService;
import br.com.victor.emprestimos.services.EmprestimoService;
import lombok.SneakyThrows;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("adm")
public class AdmController {


    private EmprestimoService emprestimoService;
    private ClienteService clienteService;

    public AdmController(EmprestimoService emprestimoService,ClienteService clienteService) {
        this.emprestimoService = emprestimoService;
        this.clienteService = clienteService;
    }


    @Transactional
    @DeleteMapping("delete-emprestimo/{id}")
    public ResponseEntity<Void> deleteEmprestimo(@RequestHeader String token, @PathVariable(name = "id") Long id) throws AuthenticationException, InvalidTokenException {
        emprestimoService.deleteEmprestimo(token,id);
        return ResponseEntity.status(HttpStatus.GONE).build();
    }

    @SneakyThrows
    @Transactional
    @PatchMapping("altera-emprestimo")
    public ResponseEntity<Void> alteraEmprestimo(@RequestHeader String token, @ModelAttribute AlteraEmprestimoRequest request) throws InvalidTokenException {
        emprestimoService.alteraEmprestimo(token,request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("seta-manager")
    public ResponseEntity<?> setaManager(@RequestHeader String token,@RequestHeader String cpf) throws InvalidTokenException {
        clienteService.setaManager(token,cpf);
        return ResponseEntity.status(201).build();
    }



}
