package es.bsmp.stockapi.security.apikeyauthentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public final class ApiKeyAuthenticationConfigurer extends AbstractHttpConfigurer<ApiKeyAuthenticationConfigurer, HttpSecurity> {

    private final ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    @Override
    public void init(HttpSecurity http) {
        http.authenticationProvider(apiKeyAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) {
        var authManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(
                new ApiKeyAuthenticationFilter(authManager), UsernamePasswordAuthenticationFilter.class
        );
    }
}
