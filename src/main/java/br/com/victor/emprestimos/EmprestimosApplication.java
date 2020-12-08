package br.com.victor.emprestimos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class EmprestimosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmprestimosApplication.class, args);
	}

}
