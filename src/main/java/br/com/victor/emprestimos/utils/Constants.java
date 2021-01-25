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
    public static final Long TOKEN_EXPIRATION_TIME_20_MIN = 600000L*2;
    public static final Long TOKEN_EXPIRATION_TIME_1_MIN = 60000L;

    public static final String TOKEN_EXPIRATION_TIME_20_MIN_CRON = "* 0/20 * * * *";
    public static final String TOKEN_EXPIRATION_TIME_1_MIN_CRON = "0/59 * * * * *";
}
