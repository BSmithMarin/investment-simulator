package es.bsmp.stockapi.security.apikeyauthentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFilter;

import java.io.IOException;
import java.util.Map;


@Log4j2
class ApiKeyAuthenticationFilter extends AuthenticationFilter {

    public ApiKeyAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager, ApiKeyAuthenticationFilter::convert);
        super.setFailureHandler(ApiKeyAuthenticationFilter::onFailure);
        super.setSuccessHandler(ApiKeyAuthenticationFilter::onSucces);
    }

    private static Authentication convert(HttpServletRequest request) {
        String apiKey = request.getParameter("apikey");

        if (apiKey == null) {
            throw new BadCredentialsException("you must provide an API KEY format apikey=YOUR_API_KEY");
        }

        return ApiKeyAuthentication.authenticationRequest(apiKey);
    }

    private static void onFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setCharacterEncoding("utf-8");

        Map<String,String> response = Map.of(
                "error",e.getMessage()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String errorRes = objectMapper.writeValueAsString(response);
        res.getWriter().println(errorRes);
    }

    private static void onSucces(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        log.info("connection from "+request.getRemoteHost()+" origin: "+request.getHeader(HttpHeaders.ORIGIN));
    }
}
