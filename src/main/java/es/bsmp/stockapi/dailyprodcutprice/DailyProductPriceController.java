package es.bsmp.stockapi.dailyprodcutprice;

import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import es.bsmp.stockapi.FinancialProduct.FinancialProductRepository;
import es.bsmp.stockapi.util.ResponseHandler;
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
@RequestMapping(path = "/api/v1/eod")
public class DailyProductPriceController {

    @Autowired
    DailyProductPriceRepository dailyProductPriceRepository;

    @Autowired
    FinancialProductRepository financialProductRepository;

    @GetMapping(path = "/{symbol}", produces = "application/json")
    public ResponseEntity<Object> getAllData(@PathVariable String symbol) {

        FinancialProduct financialProduct = financialProductRepository.findBySymbol(symbol.toUpperCase());

        if(financialProduct == null){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error","product not found"
                    ));
        }

        List<DailyProductPrice> dailyProductPriceList = dailyProductPriceRepository.findByFinancialProductOrderByDayDesc(financialProduct);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("records", dailyProductPriceList.size());
        metadata.put("symbol", symbol.toUpperCase());
        return ResponseHandler.generateResponse(metadata, HttpStatus.OK, dailyProductPriceList);
    }

}
