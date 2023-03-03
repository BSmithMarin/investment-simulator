package es.bsmp.stockapi.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import es.bsmp.stockapi.security.apikeyauthentication.ApiKeyAuthenticationConfigurer;
import es.bsmp.stockapi.security.apikeyauthentication.ApiKeyAuthenticationProvider;
import es.bsmp.stockapi.user.UserServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class ApplicationSecurityConfig {

    @Value("${jwt.key}")
    private String JWT_KEY;

    @Value("${base.url}")
    private String BASE_URL;

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http, ApiKeyAuthenticationConfigurer configurer) throws Exception {

        return http.csrf().disable()
                .authorizeHttpRequests(authorize -> {
                    authorize.anyRequest().authenticated();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).apply(configurer)
                .and()
                .build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain appFilterChain(HttpSecurity http, UserServiceImp userServiceImp) throws Exception {

        return http.csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .securityMatcher(BASE_URL+"/auth/**")
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/singup").permitAll();
                    auth.requestMatchers("api/v1/auth/login").permitAll();
                    auth.anyRequest().authenticated();
                })
                .httpBasic(withDefaults())
                .userDetailsService(userServiceImp)
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(JWT_KEY.getBytes(),"RSA"))
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(JWT_KEY.getBytes()));
    }

}
