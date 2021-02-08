package br.com.victor.emprestimos.repository;

import br.com.victor.emprestimos.domain.EmailToSent;
import br.com.victor.emprestimos.enums.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmailToSentRepository extends JpaRepository<EmailToSent,Long> {

    List<EmailToSent> findAllBySentedAndEmailType(Integer sented, EmailType emailType);

}
