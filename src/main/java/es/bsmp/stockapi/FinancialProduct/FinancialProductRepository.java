package es.bsmp.stockapi.FinancialProduct;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialProductRepository extends JpaRepository<FinancialProduct,Integer> {

    FinancialProduct findBySymbol(String symbol);
    List<FinancialProduct> findAll();
}
