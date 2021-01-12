package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.repository.PerfilRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public Perfis findById(Long id){
        return perfilRepository.findById(id).get();
    }

    @Transactional
    public void createProfiles() {
        if (perfilRepository.findAll().isEmpty()) {
            List<Perfis> perfis = new ArrayList<>();

            Perfis perfil1 = new Perfis();
            Perfis perfil2 = new Perfis();
            Perfis perfil3 = new Perfis();

            perfis.add(perfil1);
            perfis.add(perfil2);
            perfis.add(perfil3);

            perfilRepository.saveAll(perfis);

        }
    }
}
