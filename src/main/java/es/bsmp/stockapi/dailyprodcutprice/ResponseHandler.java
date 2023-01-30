package es.bsmp.stockapi.dailyprodcutprice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity<Object> generateResponse(Map<String, Object> metadata, HttpStatus status, Object responseObject) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        map.put("metadata", metadata);
        map.put("data", responseObject);
        return new ResponseEntity<>(map, status);
    }
}
