package es.bsmp.stockapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Optional<UUID> saveUser(User user) {

        if (user == null || userRepository.countByEmail(user.getEmail()) > 0) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return Optional.of(
                userRepository.save(user).getApiKey()
        );
    }

    @Override
    public Optional<User> getUserByApiKey(UUID uuid) {
        return userRepository.findOneByApiKey(uuid);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

        return userRepository.findOneByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return getUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("user with emmail: " + email + " not found")
        );
    }
}
