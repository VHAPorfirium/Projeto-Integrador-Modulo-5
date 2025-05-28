package br.com.projetoIntegrador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Para CSRF
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a configuração de segurança web do Spring
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF, comum para APIs stateless que não usam sessões baseadas em cookie
            .csrf(AbstractHttpConfigurer::disable)
            // Configura as regras de autorização para as requisições HTTP
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Permite acesso a todos os endpoints sob /api/auth/** (seus endpoints de login)
                    .requestMatchers("/api/auth/**").permitAll()
                    // Permite acesso aos endpoints do Swagger/SpringDoc
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                    // Para qualquer outra requisição, exige autenticação (você pode mudar para .permitAll() se quiser tudo aberto por enquanto)
                    .anyRequest().permitAll() // Alterado para permitir tudo por enquanto, para simplificar
            );
        return http.build();
    }

    // O PasswordEncoder bean já deve estar definido na sua classe AppConfig.java
    // Se não estiver, você pode adicioná-lo aqui também:
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
}
