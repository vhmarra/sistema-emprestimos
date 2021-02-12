package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Cliente;
import br.com.victor.emprestimos.domain.HistoricoCliente;
import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.enums.HistoricoAcoes;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.exceptions.InvalidInputException;
import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
@Transactional
@Slf4j
public class PerfilService extends TokenTheadService {

    private final PerfilRepository perfilRepository;
    private final ClienteRepository clienteRepository;
    private final HistoricoClienteRepository historicoClienteRepository;

    public PerfilService(PerfilRepository perfilRepository, ClienteRepository clienteRepository, HistoricoClienteRepository historicoClienteRepository) {
        this.perfilRepository = perfilRepository;
        this.clienteRepository = clienteRepository;
        this.historicoClienteRepository = historicoClienteRepository;
    }

    public Perfis findById(Long id){
        return perfilRepository.findById(id).get();
    }

    public void addPermissao(String clienteCpf, Long idPermission) throws InvalidCredencialsException, InvalidInputException {
        if (!getCliente().getPerfis().contains(this.findById(Constants.SUPER_ADM))) {
            throw new InvalidCredencialsException("Para adicionar permissao usuario precisar ser super adm");
        }
        Cliente clienteToAddPermission = clienteRepository.findByCpf(clienteCpf).orElse(null);

        if (clienteToAddPermission == null) {
            throw new InvalidInputException("Cliente nao encontrado");
        }
        Perfis p = perfilRepository.findById(idPermission).orElse(null);

        if (p == null) {
            throw new InvalidInputException("Permissao de id " + idPermission + " nao existe");
        }
        if(clienteToAddPermission.getPerfis().contains(p)){
            throw new InvalidInputException("Cliente ja possui esta permissao");
        }

        List<Perfis> listap = clienteToAddPermission.getPerfis();
        listap.add(p);

        clienteToAddPermission.setPerfis(listap);

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setCliente(getCliente());
        historicoCliente.setHistoricoStatus(HistoricoAcoes.ADICIONOU_PERMISSAO);
        historicoCliente.setData(LocalDateTime.now());

        clienteRepository.save(clienteToAddPermission);
        historicoClienteRepository.save(historicoCliente);

    }

    public void deletePermissao(String clienteCpf, Long idPermission) throws InvalidCredencialsException, InvalidInputException {
        if (!getCliente().getPerfis().contains(this.findById(Constants.SUPER_ADM))) {
            throw new InvalidCredencialsException("Para deletar permissao usuario precisar ser super adm");
        }
        Cliente clienteToRemoveermission = clienteRepository.findByCpf(clienteCpf).orElse(null);

        if (clienteToRemoveermission == null) {
            throw new InvalidInputException("Cliente nao encontrado");
        }
        Perfis p = perfilRepository.findById(idPermission).orElse(null);

        if (p == null) {
            throw new InvalidInputException("Permissao de id " + idPermission + " nao existe");
        }
        if(!clienteToRemoveermission.getPerfis().contains(p)){
            throw new InvalidInputException("Cliente nao possui esta permissao");
        }

        List<Perfis> listap = clienteToRemoveermission.getPerfis();
        listap.remove(p);

        HistoricoCliente historicoCliente = new HistoricoCliente();
        historicoCliente.setCliente(getCliente());
        historicoCliente.setHistoricoStatus(HistoricoAcoes.ADICIONOU_PERMISSAO);
        historicoCliente.setData(LocalDateTime.now());

        clienteRepository.save(clienteToRemoveermission);
        historicoClienteRepository.save(historicoCliente);

    }
}
