package es.bsmp.stockapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "**")
public class ErrorControler {

    @GetMapping()
    public ResponseEntity<Object> asfasfasdf(){

        return new ResponseEntity<>(Map.of("error","Route not found"),HttpStatus.OK);
    }
}
