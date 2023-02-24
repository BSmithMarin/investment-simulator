package es.bsmp.stockapi.user;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<UUID> saveUser(User user);
    Optional<User> getUserByUuid(UUID uuid);
    Optional<User> getUserByEmail(String email);


}
