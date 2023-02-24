package es.bsmp.stockapi.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByApiKey(UUID apiKey);

    Optional<User> findByEmail(String email);
}
