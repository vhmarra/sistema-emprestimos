package br.com.victor.emprestimos.utils;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;

@Data
@UtilityClass
@FieldNameConstants
public class Constants {
    public static final Long ROLE_USER = 1L;
    public static final Long ROLE_ADM = 2L;
    public static final Long SUPER_ADM = 3L;

    public static final Long TOKEN_EXPIRATION_TIME_12_HOURS = 43200000L;
    public static final String TOKEN_EXPIRATION_TIME_20_MIN = "token.expiration.20.min";
    public static final Long TOKEN_EXPIRATION_TIME_1_MIN = 60000L;

    public static final String TOKEN_EXPIRATION_TIME_20_MIN_CRON = "* 0/20 * * * *";
    public static final String TOKEN_EXPIRATION_TIME_1_MIN_CRON = "0/59 * * * * *";

    public static final String EMAIL_LOGIN = "email.login";
    public static final String EMAIL_SENHA = "email.senha";

    public static final String EMAIL_BOAS_VINDAS = "SEJA BEM VINDO {} AO SISTEMA DE EMPRESTIMOS ESPERAMOS QUE VOCE SE SINTA ACOLHIDO";
    public static final String EMAIL_BOAS_VINDAS_SUBJECT = "CADASTRO REALIZADO COM SUCESSO";

    public static final String EMAIL_TOKEN_REMOVIDO_PELO_ADM = "SEU TOKEN FOI REMOVIDO PELO ADM DO SISTEMA";

    public static final String EMAIL_EMPRESTIMO_SOLICITADO = "OLA cn SEU EMPRESTIMO NO VALOR DE vl FOI SOLICITADO";
    public static final String EMAIL_EMPRESTIMO_SOLICITADO_SUBJECT = "SOLICITACAO DE EMPRESTIMO REALIZADA COM SUCESSO";

    public static final String EMAIL_TROCA_SENHA_SUBJECT = "TROCA DE SENHA";
    public static final String EMAIL_TROCA_SENHA = "INSIRA ESTE CODIGO {} PARA EFETUAR A TROCA DE SENHA";

    public static final String EMAIL_AVISO_TROCOU_SENHA_SUBJECT = "SUA SENHA FOI ALTERADA COM SUCESSO";
    public static final String EMAIL_AVISO_TROCOU_SENHA = "OLA {} SUA SENHA FOI ALTERADA COM SUCESSO";




}
