package es.bsmp.stockapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class StockApiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(StockApiApplication.class);
    }

    @Override
    public void run(String... args) {

    }
}