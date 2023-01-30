package es.bsmp.stockapi.FinancialProduct;

import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialProductRepository extends JpaRepository<FinancialProduct,Integer> {

    FinancialProduct findBySymbol(String symbol);
}
