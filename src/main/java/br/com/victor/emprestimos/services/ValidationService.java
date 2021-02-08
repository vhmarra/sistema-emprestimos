package br.com.victor.emprestimos.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ValidationService {



    public Boolean validaCPF(String cpf){
        Pattern p = Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");
        Matcher m = p.matcher(cpf);
        if(m.matches()){
            log.info("CPF VALIDO");
           return true;
        }else{
            log.info("CPF INVALIDO");
            return false;
        }
    }

    public Boolean validaEmail(String email){
        Pattern p = Pattern.compile("/^(([^<>()[\\]\\.,;:\\s@\\\"]+(\\.[^<>()[\\]\\.,;:\\s@\\\"]+)*)|(\\\".+\\\"))@(([^<>()[\\]\\.,;:\\s@\\\"]+\\.)+[^<>()[\\]\\.,;:\\s@\\\"]{2,})$/i");
        Matcher m = p.matcher(email);
        if(m.matches()){
            log.info("EMAIL VALIDO");
            return true;
        }else{
            log.error("EMAIL INVALIDO");
            return false;
        }
    }
}
