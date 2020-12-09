package br.com.victor.emprestimos;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private Environment env;

    public SwaggerConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("br.com.victor.emprestimos.controllers")).paths(PathSelectors.any())
                .build()
                .globalOperationParameters(Arrays.asList(
                        new ParameterBuilder()
                        .name("Authorization")
                        .description("Token acesso JWT")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false).build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Sistema Emprestimos").version(env.getProperty("app.version")).build();
    }

}
