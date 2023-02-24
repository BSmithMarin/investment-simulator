package es.bsmp.stockapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "api/**")
public class ErrorController {

    @RequestMapping(method = {RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PATCH,
            RequestMethod.PUT,
            RequestMethod.DELETE})
    public ResponseEntity<Object> error() {

        return new ResponseEntity<>(Map.of("error", "Route not found"), HttpStatus.BAD_REQUEST);
    }
}
