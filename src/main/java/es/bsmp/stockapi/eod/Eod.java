package es.bsmp.stockapi.eod;


import com.fasterxml.jackson.annotation.JsonIgnore;
import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "eod")
@Data
@NoArgsConstructor
public class Eod {

    @Id
    @SequenceGenerator(sequenceName = "eod_seq",name = "daily_product_price")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_product_price" )
    @JsonIgnore
    private Long id;
    @Column(name = "day", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate day;
    @Column(name = "open_price", columnDefinition = "DECIMAL(7,3)", nullable = false)
    private double openPrice;
    @Column(name = "high_price", columnDefinition = "DECIMAL(7,3)", nullable = false)
    private double highPrice;
    @Column(name = "low_price", columnDefinition = "DECIMAL(7,3)", nullable = false)
    private double lowPrice;
    @Column(name = "dividend_per_share", columnDefinition = "DECIMAL(7,3)", nullable = false)
    private double dividendPerShare;
    @Column(name = "volume")
    private int volume;
    @Column(name = "close_price", columnDefinition = "DECIMAL(7,3)", nullable = false)
    private double closePrice;
    @ManyToOne
    @JoinColumn(name = "financial_product_id", nullable = false)
    @JsonIgnore
    private FinancialProduct financialProduct;


    public Eod(LocalDate day, double openPrice, double highPrice, double lowPrice, double closePrice, double dividendPerShare, int volume, FinancialProduct financialProductId) {
        this.day = day;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.dividendPerShare = dividendPerShare;
        this.volume = volume;
        this.financialProduct = financialProductId;
    }

}
