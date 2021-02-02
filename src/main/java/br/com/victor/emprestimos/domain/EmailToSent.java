package br.com.victor.emprestimos.domain;

import br.com.victor.emprestimos.enums.EmailType;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "email_to_sent")
@NoArgsConstructor
@AllArgsConstructor
public class EmailToSent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "date_sented")
    private LocalDateTime dateSented;

    @Column(name = "email_type")
    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    @ManyToOne
    private Cliente cliente;

    @Column(name = "sented")
    private Integer sented;

}
