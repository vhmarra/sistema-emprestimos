package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.AlteraDados;
import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.exceptions.InvalidTokenException;
import br.com.victor.emprestimos.repository.AlteraDadosRepository;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Data
public class AlteraDadosService extends TokenTheadService {

    private final ClienteRepository clienteRepository;
    private final EmailService emailService;
    @Qualifier(value = "db2")
    private final AlteraDadosRepository alteraDadosRepository;
    private final HistoricoClienteRepository historicoClienteRepository;

    public AlteraDadosService(ClienteRepository clienteRepository, EmailService emailService, AlteraDadosRepository alteraDadosRepository, HistoricoClienteRepository historicoClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
        this.alteraDadosRepository = alteraDadosRepository;
        this.historicoClienteRepository = historicoClienteRepository;
    }

    public void solicitaAlteraSenha(String cpf) throws Exception {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElse(null);
        if(cliente == null){
            throw new InvalidInputException("Cliente nao encontrado");
        }

        alteraDadosRepository.findAllByClienteId(cliente.getId()).forEach(token->{
            token.setDateUpdated(LocalDateTime.now());
            token.setUsed(1);
            alteraDadosRepository.save(token);
        });

        String token = UUID.randomUUID().toString() + DigestUtils.sha1Hex(cliente.getCpf()+cliente.getEmail()+cliente.getNome());
        emailService.sendEmail(cliente.getEmail(), cliente.getNome(), Constants.EMAIL_TROCA_SENHA_SUBJECT, Constants.EMAIL_TROCA_SENHA.replace("{}", token));

        AlteraDados alteraDados = new AlteraDados();
        alteraDados.setCliente(cliente);
        alteraDados.setDateCreated(LocalDateTime.now());
        alteraDados.setTokenTrocaDados(token);
        alteraDados.setUsed(0);

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setData(LocalDateTime.now());
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoAcoes.SOLICITOU_ALTERACAO_DE_SENHA);

        alteraDadosRepository.save(alteraDados);
        historicoClienteRepository.save(historicoCliente);

        log.info("token para alteracao de senha {}",token);

    }

    public void alteraSenha(String t,String novaSenha) throws Exception {
        AlteraDados token = alteraDadosRepository.findByTokenTrocaDados(t);
        Cliente cliente = clienteRepository.findById(token.getCliente().getId()).orElse(null);

        if(token.getUsed() == 1){
            throw new InvalidTokenException("Token de alteracao invalido");
        }
        if(cliente == null){
            throw new InvalidInputException("Cliente nao encontrado");
        }
        cliente.setSenha(DigestUtils.sha512Hex(novaSenha));
        token.setUsed(1);
        token.setDateUpdated(LocalDateTime.now());

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setData(LocalDateTime.now());
        historicoCliente.setCliente(cliente);
        historicoCliente.setHistoricoStatus(HistoricoAcoes.ALTEROU_SENHA);

        clienteRepository.save(cliente);
        alteraDadosRepository.save(token);
        historicoClienteRepository.save(historicoCliente);
        emailService.sendEmail(cliente.getEmail(),cliente.getNome(),
                Constants.EMAIL_AVISO_TROCOU_SENHA_SUBJECT,Constants.EMAIL_AVISO_TROCOU_SENHA.replace("{}",cliente.getNome().toUpperCase()));
    }

}
