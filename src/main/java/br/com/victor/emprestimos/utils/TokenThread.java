package br.com.victor.emprestimos.utils;

import br.com.victor.emprestimos.domain.TokenCliente;
import lombok.Data;

@Data
public final class TokenThread {

    private static ThreadLocal<TokenCliente> tClocal = new ThreadLocal<>();

    private TokenThread(){
        super();
    }
    public static void setToken(TokenCliente token) {
        tClocal.set(token);
    }

    public static void removeToken() {
        tClocal.remove();
    }

    public static ThreadLocal<TokenCliente> getToken() {
        return tClocal;
    }
}
