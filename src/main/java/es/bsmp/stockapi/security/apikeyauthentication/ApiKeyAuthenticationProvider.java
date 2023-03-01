package es.bsmp.stockapi.security.apikeyauthentication;

import es.bsmp.stockapi.user.User;
import es.bsmp.stockapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        var authRequest = (ApiKeyAuthentication) authentication;
        UUID apiKey;
        User user = null;
        try {
            apiKey = UUID.fromString(authRequest.getCredentials());
            user = userService.getUserByApiKey(apiKey)
                    .orElse(null);
        } catch (IllegalArgumentException ignored) {

        }

        if (user == null) {
            throw new BadCredentialsException("invalid api key");
        } else {
            return ApiKeyAuthentication.authenticate();
        }
    }

    //Only accepts ApiKeyAuthentications
    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.isAssignableFrom(authentication);
    }
}
