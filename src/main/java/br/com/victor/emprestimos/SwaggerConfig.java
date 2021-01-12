package br.com.victor.emprestimos;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Sistema Emprestimos").version(env.getProperty("app.version")).build();
    }





//    private List<Parameter> globalParameterList() {
//        val authTokenHeader =
//                new ParameterBuilder()
//                        .name("TOKEN") // name of the header
//                        .modelRef(new ModelRef("string")) // data-type of the header
//                        .required(true) // required/optional
//                        .parameterType("header") // for query-param, this value can be 'query'
//                        .description("Token")
//                        .build();
//
//        return Collections.singletonList(authTokenHeader);
//    }

//    @Bean
//    public Docket docket() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .forCodeGeneration(true)
//                .globalOperationParameters(globalParameterList())
//                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
//                .paths(PathSelectors.any())
//                .build();
//    }


}
