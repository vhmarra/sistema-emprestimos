package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.repository.ClienteRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class TokenService {

    @Value("${emprestimos.jwt.expiration}")
    private String expiration;

    @Value("${emprestimos.jwt.secret}")
    private String secret;

    private final ClienteRepository repository;

    public TokenService(ClienteRepository repository) {
        this.repository = repository;
    }

    public String geraToken(Authentication authentication) {
        Cliente cliente = (Cliente) authentication.getPrincipal();
        Date hoje = new Date();
        Date expirationDate = new Date(hoje.getTime()+Long.parseLong(expiration));
        String token = Jwts.builder()
                .setIssuer("API Sistema de Emprestimos")
                .setSubject(cliente.getId().toString())
                .setIssuedAt(hoje)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();

        cliente.setToken(token);
        repository.save(cliente);

        return token;
    }

    public boolean isTokenValid(String token){
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Long getClienteId(String token){
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

}
