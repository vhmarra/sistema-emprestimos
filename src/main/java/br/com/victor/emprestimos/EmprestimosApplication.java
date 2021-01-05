package br.com.victor.emprestimos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.Entity;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackages = {"br.com.victor.emprestimos.domain"})
public class EmprestimosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmprestimosApplication.class, args);
	}

}
