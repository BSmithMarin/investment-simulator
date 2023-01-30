package es.bsmp.stockapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.bsmp.stockapi.dailyprodcutprice.DailyProductPriceRepository;
import es.bsmp.stockapi.FinancialProduct.FinancialProductRepository;
import es.bsmp.stockapi.dailyprodcutprice.DailyProductPrice;
import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class FillData {

    @Autowired
    Environment environment;
    @Autowired
    DailyProductPriceRepository dailyProductPriceRepository;
    @Autowired
    FinancialProductRepository financialProductRepository;

    void fillInitialData() {

        List<FinancialProduct> financialProducts = financialProductRepository.findAll();

        for (FinancialProduct financialProduct : financialProducts) {

            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("www.alphavantage.co")
                    .path("/query")
                    .query("function=TIME_SERIES_DAILY_ADJUSTED")
                    .queryParam("symbol", financialProduct.getSymbol())
                    .queryParam("outputsize", "full")
                    .queryParam("apikey", environment.getProperty("aphavantage.key"))
                    .build().toUri();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .timeout(Duration.ofSeconds(5L))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(5L))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::insertDataInDb);
        }

    }

    void insertDataInDb(String json) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.get("Time Series (Daily)").fields();
            String symbol = rootNode.get("Meta Data").get("2. Symbol").asText();
            List<DailyProductPrice> dailyProductPriceList = new ArrayList<>(5000);

            FinancialProduct financialProduct = financialProductRepository.findBySymbol(symbol);

            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> element = iterator.next();

                dailyProductPriceList.add(new DailyProductPrice(
                        LocalDate.parse(element.getKey()),
                        element.getValue().get("1. open").asDouble(),
                        element.getValue().get("2. high").asDouble(),
                        element.getValue().get("3. low").asDouble(),
                        element.getValue().get("4. close").asDouble(),
                        element.getValue().get("7. dividend amount").asDouble(),
                        element.getValue().get("6. volume").asInt(),
                        financialProduct
                ));

            }
            System.out.println(symbol +": "+ dailyProductPriceList.size());
            Instant start = Instant.now();
            dailyProductPriceRepository.saveAll(dailyProductPriceList);
            dailyProductPriceRepository.flush();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start,finish).toMillis();
            System.out.println(symbol+" time elapsed: "+timeElapsed/1000+" seconds");

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing the json content");
            throw new RuntimeException(e);
        }
    }

}
