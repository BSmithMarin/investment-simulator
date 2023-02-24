package es.bsmp.stockapi.eod;

import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EodRepository extends JpaRepository<Eod,Integer> {

    List<Eod> findByFinancialProductOrderByDayDesc(FinancialProduct financialProduct);

    @Query("select d from Eod d where d.financialProduct = ?1 order by d.day desc limit 1")
    Eod findLastDailyProductPriceByFinancialProduct(FinancialProduct financialProduct);
}
