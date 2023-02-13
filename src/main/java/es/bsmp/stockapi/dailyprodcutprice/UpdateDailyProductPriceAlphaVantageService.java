package es.bsmp.stockapi.dailyprodcutprice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import es.bsmp.stockapi.FinancialProduct.FinancialProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UpdateDailyProductPriceAlphaVantageService {

    Environment environment;
    DailyProductPriceRepository dailyProductPriceRepository;
    FinancialProductRepository financialProductRepository;
    private static final Logger logger = LogManager.getLogger(UpdateDailyProductPriceAlphaVantageService.class);
    private final Queue<FinancialProduct> queue;
    ScheduledExecutorService executorService;

    @Autowired
    public UpdateDailyProductPriceAlphaVantageService(Environment environment, DailyProductPriceRepository dailyProductPriceRepository,
                                                      FinancialProductRepository financialProductRepository, ScheduledExecutorService executorService) {
        this.environment = environment;
        this.dailyProductPriceRepository = dailyProductPriceRepository;
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
            logger.error("Error adding all financial products to the queue");
        }

    }

    /**
     * Retrieves one financial product from the queue and updates the daily_product_price
     */
    void saveFinancialProductData() {

        FinancialProduct financialProduct = queue.poll();
        if (financialProduct == null)
            return;

        DailyProductPrice lastRecord= dailyProductPriceRepository
                .findLastDailyProductPriceByFinancialProduct(financialProduct);

        boolean fullOutputSize = lastRecord == null;

        try {
            String response = getDataFromAlphaVantage(financialProduct,fullOutputSize);
            List<DailyProductPrice> dailyProductPriceList = mapAlphaVantageResponseToDailyProductPrice(response,financialProduct);

            if (!fullOutputSize){
                dailyProductPriceList = dailyProductPriceList.stream()
                                .filter((d)-> (d.getDay().isAfter(lastRecord.getDay())))
                                .toList();
            }
            dailyProductPriceRepository.saveAll(dailyProductPriceList);
            dailyProductPriceRepository.flush();

        }catch (Exception e){
            logger.error("error retrieving data for "+financialProduct.getSymbol());
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
            logger.error("Error recuperando los datos desde AlphaVantage.co");
            throw new RuntimeException(e);
        }

        return response;
    }

    List<DailyProductPrice> mapAlphaVantageResponseToDailyProductPrice(String json,FinancialProduct financialProduct) {

        List<DailyProductPrice> dailyProductPriceList;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.get("Time Series (Daily)").fields();

            dailyProductPriceList = new ArrayList<>(1000);
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> element = iterator.next();

                dailyProductPriceList.add(new DailyProductPrice(
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
        return  dailyProductPriceList;
    }
}