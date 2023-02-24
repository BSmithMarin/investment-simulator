package es.bsmp.stockapi.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;


final class ApiKeyAuthenticationConfigurer extends AbstractHttpConfigurer<ApiKeyAuthenticationConfigurer, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) {
        var validKeys = Set.of("asdf","qwer","zxcv");
        http.authenticationProvider(new ApiKeyAuthenticationProvider(validKeys));
    }

    @Override
    public void configure(HttpSecurity http) {

        var authManager = http.getSharedObject(AuthenticationManager.class);

        http.addFilterBefore(
                new ApiKeyAuthenticationFilter(authManager), UsernamePasswordAuthenticationFilter.class
        );
    }
}
