package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.repository.ClienteRepository;
import lombok.SneakyThrows;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TokenFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private ClienteRepository repository;

    public TokenFilter(TokenService tokenService, ClienteRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        boolean validToken = tokenService.isTokenValid(token);
        if(validToken){
            autenticaCliente(token);
        }
        filterChain.doFilter(request,response);
    }

    private void autenticaCliente(String token) throws AuthenticationException {
        Cliente cliente = repository.findById(tokenService.getClienteId(token)).orElse(null);
        if(cliente==null){
            throw new AuthenticationException("Cliente invalido");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(cliente,null,cliente.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        if(token == null || token.isEmpty() || !token.startsWith("Bearer ")){
            return null;
        }

        return token.substring(7,token.length());
    }
}
