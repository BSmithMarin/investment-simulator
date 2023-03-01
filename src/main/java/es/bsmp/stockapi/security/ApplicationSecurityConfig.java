package es.bsmp.stockapi.security;

import es.bsmp.stockapi.security.apikeyauthentication.ApiKeyAuthenticationConfigurer;
import es.bsmp.stockapi.security.apikeyauthentication.ApiKeyAuthenticationProvider;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
class ApplicationSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http, ApiKeyAuthenticationConfigurer configurer) throws Exception {

        http.csrf().disable()
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> {
                    authorize.anyRequest().authenticated();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).apply(configurer);

        return http.build();
    }

    @Bean
    SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/singup", "/login").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

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

    @Bean
    ApiKeyAuthenticationConfigurer apiKeyAuthenticationConfigurer(ApiKeyAuthenticationProvider authenticationProvider) {
        return new ApiKeyAuthenticationConfigurer(authenticationProvider);
    }

}
