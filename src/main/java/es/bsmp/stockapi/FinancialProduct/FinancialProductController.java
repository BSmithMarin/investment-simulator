package es.bsmp.stockapi.FinancialProduct;

import es.bsmp.stockapi.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1")
public class FinancialProductController {
    @Autowired
    FinancialProductRepository financialProductRepository;

    @GetMapping(path = "/symbols")
    public ResponseEntity<Object> getAll() {
        List<FinancialProduct> financialProducts = financialProductRepository.findAll();
        Map<String,Object> metadata = new HashMap<>();
        metadata.put("records",financialProducts.size());
        return ResponseHandler.generateResponse(metadata, HttpStatus.OK, financialProducts);
    }
}
