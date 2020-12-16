package br.com.victor.emprestimos.dtos;

import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
public class LoginClientRequest {

    private String cpf;
    private String senha;


    public UsernamePasswordAuthenticationToken convertToAuth(){
        return new UsernamePasswordAuthenticationToken(cpf,senha);
    }

}
