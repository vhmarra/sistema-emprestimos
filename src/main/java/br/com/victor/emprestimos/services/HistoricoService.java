package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.dtos.HistoricoClienteDTO;
import br.com.victor.emprestimos.exceptions.ForbiddenException;
import br.com.victor.emprestimos.repository.HistoricoClienteRepository;
import br.com.victor.emprestimos.utils.Constants;
import br.com.victor.emprestimos.utils.TokenTheadService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@Service
public class HistoricoService extends TokenTheadService {

    private final HistoricoClienteRepository historicoClienteRepository;
    private final PerfilService perfilService;

    public HistoricoService(HistoricoClienteRepository historicoClienteRepository,PerfilService perfilService) {
        this.historicoClienteRepository = historicoClienteRepository;
        this.perfilService = perfilService;
    }

    public List<HistoricoClienteDTO> getAllHistorico() throws ForbiddenException {
        if(!getCliente().getPerfis().contains(perfilService.findById(Constants.SUPER_ADM))){
            throw new ForbiddenException("Usuario sem permissao");
        }

        List<HistoricoClienteDTO> historicoDto = new ArrayList<>();
        historicoClienteRepository.findAll().forEach(h -> {
            HistoricoClienteDTO dto = new HistoricoClienteDTO();
            dto.setId(h.getId());
            dto.setData(h.getData().toString());
            dto.setIdCliente(h.getCliente().getId());
            dto.setStatus(h.getHistoricoStatus().toString());
            dto.setClienteNome(h.getCliente().getNome());
            historicoDto.add(dto);
        });;

        return historicoDto;
    }


}
