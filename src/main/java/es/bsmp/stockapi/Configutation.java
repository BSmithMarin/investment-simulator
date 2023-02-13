package es.bsmp.stockapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class Configutation {

    @Bean
    ScheduledExecutorService executorService(){
        return Executors.newSingleThreadScheduledExecutor();
    }
}
