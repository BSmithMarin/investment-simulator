package es.bsmp.stockapi.dailyprodcutprice;

import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyProductPriceRepository extends JpaRepository<DailyProductPrice,Integer> {

    List<DailyProductPrice> findByFinancialProduct(FinancialProduct financialProduct);
}
