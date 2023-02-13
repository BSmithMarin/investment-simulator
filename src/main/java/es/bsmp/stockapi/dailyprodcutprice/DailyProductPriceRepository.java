package es.bsmp.stockapi.dailyprodcutprice;

import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyProductPriceRepository extends JpaRepository<DailyProductPrice,Integer> {

    List<DailyProductPrice> findByFinancialProductOrderByDayDesc(FinancialProduct financialProduct);

    @Query("select d from DailyProductPrice d where d.financialProduct = ?1 order by d.day desc limit 1")
    DailyProductPrice findLastDailyProductPriceByFinancialProduct(FinancialProduct financialProduct);
}
