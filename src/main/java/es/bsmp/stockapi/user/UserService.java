package es.bsmp.stockapi.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    Optional<UUID> saveUser(User user);
    Optional<User> getUserByApiKey(UUID apiKey);
    Optional<User> getUserByEmail(String email);


}
