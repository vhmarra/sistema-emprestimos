package br.com.victor.emprestimos.controllers;

import br.com.victor.emprestimos.services.EmprestimoService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("adm")
public class AdmController {


    private final EmprestimoService emprestimoService;

    public AdmController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }


    @Transactional
    @DeleteMapping("delete-emprestimo/{id}")
    public ResponseEntity<Void> deleteEmprestimo(@RequestHeader String token, @PathVariable(name = "id") Long id) throws AuthenticationException {
        emprestimoService.deleteEmprestimo(token,id);
        return ResponseEntity.status(HttpStatus.GONE).build();
    }



}
