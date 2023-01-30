package es.bsmp.stockapi.dailyprodcutprice;

import es.bsmp.stockapi.FinancialProduct.FinancialProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/product_data")
public class DailyProductPriceController {

    @Autowired
    DailyProductPriceRepository dailyProductPriceRepository;

    @Autowired
    FinancialProductRepository financialProductRepository;

    @GetMapping(path = "/{symbol}", produces = "application/json")
    public ResponseEntity<Object> getAllData(@PathVariable String symbol) {


        List<DailyProductPrice> dailyProductPriceList = dailyProductPriceRepository.findByFinancialProduct(
                financialProductRepository.findBySymbol(symbol));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("records", dailyProductPriceList.size());
        metadata.put("symbol", symbol);
        return ResponseHandler.generateResponse(metadata, HttpStatus.OK, dailyProductPriceList);
    }

}
