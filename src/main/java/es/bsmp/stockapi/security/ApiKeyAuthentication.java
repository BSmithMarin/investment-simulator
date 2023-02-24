package es.bsmp.stockapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class ApiKeyAuthentication implements Authentication {

    private final List<? extends GrantedAuthority> authorities;
    private final boolean authenticated;
    private final String apiKey;

    private ApiKeyAuthentication(List<? extends GrantedAuthority> authorities, String apiKey, boolean authenticated) {
        this.authorities = Collections.unmodifiableList(authorities);
        this.apiKey = apiKey;
        this.authenticated = authenticated;
    }

    static ApiKeyAuthentication authenticationRequest(String apiKey) {
        return new ApiKeyAuthentication(Collections.emptyList(), apiKey, false);
    }

    static ApiKeyAuthentication authenticate() {
        return new ApiKeyAuthentication(
                List.of(new SimpleGrantedAuthority("USER")),
                null,
                true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Can not set athenticated state manually");
    }

    @Override
    public String getName() {
        return null;
    }
}
