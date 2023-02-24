package es.bsmp.stockapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;


    @Override
    public Optional<UUID> saveUser(User user) {

        if (user == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                userRepository.save(user).getApiKey()
        );
    }

    @Override
    public Optional<User> getUserByUuid(UUID uuid) {
        return userRepository.findByApiKey(uuid);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }
}
