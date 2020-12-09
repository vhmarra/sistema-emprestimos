package br.com.victor.emprestimos;

import br.com.victor.emprestimos.repository.ClienteRepository;
import br.com.victor.emprestimos.services.AutenticacaoService;
import br.com.victor.emprestimos.services.TokenFilter;
import br.com.victor.emprestimos.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AutenticacaoService service;
    private final TokenService tokenService;
    private final ClienteRepository repository;

    public SecurityConfig(AutenticacaoService service, TokenService tokenService, ClienteRepository repository) {
        this.service = service;
        this.tokenService = tokenService;
        this.repository = repository;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/cliente/login").permitAll()
                .antMatchers(HttpMethod.POST,"/cliente/cadastro").permitAll()
                .antMatchers(HttpMethod.POST,"/cliente/solicita-emprestimo").hasRole("CLIENTE")
                .anyRequest().authenticated().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(new TokenFilter(tokenService,repository), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**");
    }




}
