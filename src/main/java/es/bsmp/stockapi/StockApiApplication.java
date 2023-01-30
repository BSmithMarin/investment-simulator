package es.bsmp.stockapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class StockApiApplication implements CommandLineRunner {


    @Autowired
    FillData fillData;
    public static void main(String[] args) {
        SpringApplication.run(StockApiApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {

//        fillData.fillInitialData();
    }
}