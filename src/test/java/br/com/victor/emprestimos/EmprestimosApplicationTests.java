package br.com.victor.emprestimos;

import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.repository.EmprestimoRepository;
import br.com.victor.emprestimos.repository.HistoricoRepository;
import br.com.victor.emprestimos.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmprestimosApplicationTests {

	@Test
	void contextLoads() {
	}

}
