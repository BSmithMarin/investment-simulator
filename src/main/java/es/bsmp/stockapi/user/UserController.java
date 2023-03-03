package es.bsmp.stockapi.user;

import es.bsmp.stockapi.security.jwtauthentication.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "${base.url}/auth")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping(
            path = "/singup",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@RequestBody User user) {

        log.info(user);

        user.setApiKey(UUID.randomUUID());

        Optional<UUID> apiKey = userService.saveUser(user);

        if (apiKey.isPresent()) {
            return ResponseEntity.ok()
                    .body(Map.of("api_key", apiKey.get()));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "invalid email"));
        }

    }

    @PostMapping(
            value = "/token"
    )
    public String token(Authentication authentication) {

        return tokenService.generateToken(authentication);
    }

    @PostMapping("/test")
    public String test(){
        return "athenticated";
    }

}
