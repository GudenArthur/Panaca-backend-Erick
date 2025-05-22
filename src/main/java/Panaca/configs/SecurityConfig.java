package Panaca.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (usando JWT)
            .csrf(csrf -> csrf.disable())

            // 2. Configurar sesión sin estado
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 3. Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // 3.1 Endpoints públicos
                .requestMatchers(
                    "/api/auth/**", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**"
                ).permitAll()

                // 3.2 Nuevo: solo ADMIN puede acceder al panel de métricas
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 3.3 El resto de endpoints requieren autenticación
                .anyRequest().authenticated()
            )

            // 4. Inyectar tu filtro JWT antes del de usuario/clave
            .addFilterBefore(
                jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
