package es.bsmp.stockapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;


class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final Set<String> validKeys;

    public ApiKeyAuthenticationProvider(Set<String> validKeys) {
        this.validKeys = validKeys;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var authRequest = (ApiKeyAuthentication) authentication;

        if (validKeys.contains(authRequest.getCredentials())) {
            return ApiKeyAuthentication.authenticate();
        } else {
            throw new BadCredentialsException("invalid api key");
        }
    }

    //Only accepts ApiKeyAuthentications
    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.isAssignableFrom(authentication);
    }
}
