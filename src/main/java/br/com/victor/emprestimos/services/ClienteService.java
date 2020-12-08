package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.dtos.CadastraClienteRequest;
import br.com.victor.emprestimos.repository.ClienteRepository;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public void cadastraCliente(CadastraClienteRequest request){
        Cliente cliente = new Cliente();

        String psw = new BCryptPasswordEncoder().encode(request.getSenha());

        cliente.setCpf(request.getCpf());
        cliente.setDataNascimento(LocalDate.parse(request.getDataNascimento(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        cliente.setSenha(psw);
        cliente.setNome(request.getNome());
        cliente.setScoreCredito(request.getScoreCredito());

        clienteRepository.save(cliente);

    }





}
