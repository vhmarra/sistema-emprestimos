package br.com.victor.emprestimos.utils;

import lombok.Data;

@Data
public class Constants {
    public static final Long ROLE_USER = 1L;
    public static final Long ROLE_ADM = 2L;
    public static final Long SUPER_ADM = 3L;
    public static final Long TOKEN_EXPIRATION_TIME_12_HOURS = 43200000L;
}
