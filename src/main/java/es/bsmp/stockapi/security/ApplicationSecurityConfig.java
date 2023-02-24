package es.bsmp.stockapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/singup")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new ApiKeyAuthenticationConfigurer())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(withDefaults());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        final UrlBasedCorsConfigurationSource configuration = new UrlBasedCorsConfigurationSource();

        CorsConfiguration apiCorsConfiguration = new CorsConfiguration();

        apiCorsConfiguration.addAllowedMethod(HttpMethod.GET);
        apiCorsConfiguration.addAllowedMethod(HttpMethod.POST);
        apiCorsConfiguration.addAllowedMethod(HttpMethod.PUT);
        apiCorsConfiguration.addAllowedMethod(HttpMethod.PATCH);
        apiCorsConfiguration.addAllowedOrigin("http://bsmp.es");

        configuration.registerCorsConfiguration("/**", apiCorsConfiguration);

        return configuration;
    }


}
