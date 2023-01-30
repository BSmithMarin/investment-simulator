package es.bsmp.stockapi.dailyprodcutprice;


import com.fasterxml.jackson.annotation.JsonIgnore;
import es.bsmp.stockapi.FinancialProduct.FinancialProduct;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_product_price")
public class DailyProductPrice {

    @Id
    @SequenceGenerator(sequenceName = "daily_product_price_seq",name = "daily_product_price",allocationSize = 53)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_product_price" )
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
    private FinancialProduct financialProduct;

    protected DailyProductPrice() {
    }

    public DailyProductPrice(LocalDate day, double openPrice, double highPrice, double lowPrice, double closePrice, double dividendPerShare, int volume, FinancialProduct financialProductId) {
        this.day = day;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.dividendPerShare = dividendPerShare;
        this.volume = volume;
        this.financialProduct = financialProductId;
    }
    @JsonIgnore
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(double dividendPerShare) {
        this.dividendPerShare = dividendPerShare;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @JsonIgnore
    public FinancialProduct getFinancialProduct() {
        return financialProduct;
    }

    public void setFinancialProduct(FinancialProduct financialProduct) {
        this.financialProduct = financialProduct;
    }

    @Override
    public String toString() {
        return "DailyProductPrice{" +
                "id=" + id +
                ", day=" + day +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", dividendPerShare=" + dividendPerShare +
                ", volume=" + volume +
                ", closePrice=" + closePrice +
                ", financialProduct=" + financialProduct +
                '}';
    }
}
