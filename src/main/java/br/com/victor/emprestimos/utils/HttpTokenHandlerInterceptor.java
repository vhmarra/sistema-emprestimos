package br.com.victor.emprestimos.utils;

import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.dtos.ClienteTokenDTO;
import br.com.victor.emprestimos.exceptions.ForbiddenException;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


//TODO ver se tem outras formas de fazer esse interceptor
@Service
@Slf4j
public class HttpTokenHandlerInterceptor extends WebRequestHandlerInterceptorAdapter {


    @Autowired
    public TokenRepository repository;
    @Autowired
    public WebRequestInterceptor requestInterceptor;

    public HttpTokenHandlerInterceptor(WebRequestInterceptor requestInterceptor, TokenRepository repository) {
        super(requestInterceptor);
        this.repository = repository;
    }

    @Bean
    public HttpTokenHandlerInterceptor myCustomHandlerInterceptor() {
        return new HttpTokenHandlerInterceptor(requestInterceptor, repository);
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HttpTokenHandlerInterceptor(requestInterceptor, repository))
                        .addPathPatterns("/**")
                        .excludePathPatterns(
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/auth/**"
                        );

            }
        };
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("URL -> {}", request.getServletPath());
        if (request.getServletPath().contains("/cliente")) {
            String tokenHeader = request.getHeader("token");
            log.info("INTERCEPTANDO TOKEN {}",request.getHeader("token"));
            Optional<TokenCliente> tokenCliente = repository.findByToken(tokenHeader);
            if(!tokenCliente.isPresent()){
                throw new ForbiddenException("TOKEN NAO ESTA PRESENTE");
            }
            if (tokenCliente.get().getAtivo() == false) {
                throw new InvalidCredencialsException("TOKEN ESTA INVALIDADO");
            }
            TokenThread.setToken(tokenCliente.get());
            log.info("TOKEN SETADO NA THREAD PARA O CLIENTE {}", ClienteTokenDTO.converte(tokenCliente.get().getCliente()));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }



}

