package es.bsmp.stockapi.eod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import es.bsmp.stockapi.FinancialProduct.FinancialProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class UpdateEodFromAlphaVantageService {

    Environment environment;
    EodRepository eodRepository;
    FinancialProductRepository financialProductRepository;
    private final Queue<FinancialProduct> queue;
    ScheduledExecutorService executorService;

    @Autowired
    public UpdateEodFromAlphaVantageService(Environment environment, EodRepository eodRepository,
                                            FinancialProductRepository financialProductRepository, ScheduledExecutorService executorService) {
        this.environment = environment;
        this.eodRepository = eodRepository;
        this.financialProductRepository = financialProductRepository;
        this.queue = new ArrayDeque<>();
        this.executorService = executorService;
        executorService.scheduleAtFixedRate(this::addAllFinancialProductsToQueue,0L,1L, TimeUnit.DAYS);
        executorService.scheduleAtFixedRate(this::saveFinancialProductData,5L,15L, TimeUnit.SECONDS);
    }

    void addAllFinancialProductsToQueue() {
        try{
            queue.addAll(financialProductRepository.findAll());
        }catch (Exception e){
            log.error("Error adding all financial products to the queue");
        }

    }

    /**
     * Retrieves one financial product from the queue and updates the daily_product_price
     */
    void saveFinancialProductData() {

        FinancialProduct financialProduct = queue.poll();
        if (financialProduct == null)
            return;

        Eod lastRecord= eodRepository
                .findLastDailyProductPriceByFinancialProduct(financialProduct);

        boolean fullOutputSize = lastRecord == null;

        try {
            String response = getDataFromAlphaVantage(financialProduct,fullOutputSize);
            List<Eod> eodList = mapAlphaVantageResponseToDailyProductPrice(response,financialProduct);

            if (!fullOutputSize){
                eodList = eodList.stream()
                                .filter((d)-> (d.getDay().isAfter(lastRecord.getDay())))
                                .toList();
            }
            eodRepository.saveAll(eodList);
            eodRepository.flush();

        }catch (Exception e){
            log.error("error retrieving data for "+financialProduct.getSymbol());
        }

    }

    /***
     * Makes the http call for the given financial product
     * @param financialProduct
     * @return the body of the http call, format: application/json
     */
    String getDataFromAlphaVantage(FinancialProduct financialProduct, boolean fullOutputSize) {

        String response;
        String outputSize = fullOutputSize?"full":"compact";

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.alphavantage.co")
                .path("/query")
                .query("function=TIME_SERIES_DAILY_ADJUSTED")
                .queryParam("symbol", financialProduct.getSymbol())
                .queryParam("outputsize", outputSize)
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

        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            log.error("Error recuperando los datos desde AlphaVantage.co");
            throw new RuntimeException(e);
        }

        return response;
    }

    List<Eod> mapAlphaVantageResponseToDailyProductPrice(String json, FinancialProduct financialProduct) {

        List<Eod> eodList;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.get("Time Series (Daily)").fields();

            eodList = new ArrayList<>(1000);
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> element = iterator.next();

                eodList.add(new Eod(
                        LocalDate.parse(element.getKey(), DateTimeFormatter.ISO_LOCAL_DATE),
                        element.getValue().get("1. open").asDouble(),
                        element.getValue().get("2. high").asDouble(),
                        element.getValue().get("3. low").asDouble(),
                        element.getValue().get("4. close").asDouble(),
                        element.getValue().get("7. dividend amount").asDouble(),
                        element.getValue().get("6. volume").asInt(),
                        financialProduct
                ));
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing the json content");
            throw new RuntimeException(e);
        }
        return eodList;
    }
}