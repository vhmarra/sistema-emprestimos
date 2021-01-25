package br.com.victor.emprestimos.utils;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.TokenCliente;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Data
public class TokenTheadService {

    public String getToken(){
        return TokenThread.getToken().get().getToken();
    }

    public Cliente getCliente(){
        return TokenThread.getToken().get().getCliente();
    }

    public TokenCliente getTokenEntity(){
        return TokenThread.getToken().get();
    }
}
