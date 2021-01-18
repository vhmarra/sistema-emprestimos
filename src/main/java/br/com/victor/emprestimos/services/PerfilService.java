package br.com.victor.emprestimos.services;

import br.com.victor.emprestimos.domain.Perfis;
import br.com.victor.emprestimos.repository.PerfilRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@Transactional
@Slf4j
public class PerfilService {

    private PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }


    public Perfis findById(Long id){
        return perfilRepository.findById(id).get();
    }

    public void createProfiles() {
        if (perfilRepository.findAll().isEmpty()) {
            log.info("criando perfis");
            List<Perfis> perfis = new ArrayList<>();

            Perfis perfil1 = new Perfis();
            Perfis perfil2 = new Perfis();
            Perfis perfil3 = new Perfis();

            perfil1.setNome("user");
            perfil2.setNome("adm");
            perfil3.setNome("super_adm");

            perfis.add(perfil1);
            perfis.add(perfil2);
            perfis.add(perfil3);

            perfilRepository.saveAll(perfis);
            log.info("perfis criado");
        }else {
            log.info("perfis ja existentes");
        }
    }
}
