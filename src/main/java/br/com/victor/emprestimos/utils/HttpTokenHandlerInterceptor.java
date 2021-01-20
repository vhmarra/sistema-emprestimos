package br.com.victor.emprestimos.utils;

import br.com.victor.emprestimos.domain.TokenCliente;
import br.com.victor.emprestimos.exceptions.InvalidCredencialsException;
import br.com.victor.emprestimos.repository.TokenRepository;
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

@Service
public class HttpTokenHandlerInterceptor extends WebRequestHandlerInterceptorAdapter {


    private final TokenRepository repository;
    private final WebRequestInterceptor requestInterceptor;

    public HttpTokenHandlerInterceptor(TokenRepository repository, WebRequestInterceptor requestInterceptor) {
        super(requestInterceptor);
        this.repository = repository;
        this.requestInterceptor = requestInterceptor;
    }

    @Bean
    public HttpTokenHandlerInterceptor myCustomHandlerInterceptor() {
        return new HttpTokenHandlerInterceptor(repository, requestInterceptor);
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HttpTokenHandlerInterceptor(repository, requestInterceptor));
            }
        };
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InvalidCredencialsException {
        if (request.getRequestURI().contains("/cliente")) {
            Optional<TokenCliente> token = repository.findByToken(request.getHeader("token"));
            if (token.isEmpty()) {
                throw new InvalidCredencialsException("Token invalido");
            }
            TokenThread.setToken(token.get());
            return true;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws InvalidCredencialsException {

    }

}
