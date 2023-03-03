package es.bsmp.stockapi.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findOneByApiKey(UUID apiKey);

    Optional<User> findOneByEmail(String email);

    int countByEmail(String email);
}
