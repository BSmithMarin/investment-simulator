package es.bsmp.stockapi.FinancialProduct;


import jakarta.persistence.*;

@Entity
@Table(name = "financial_product")
public class FinancialProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "isin", nullable = false)
    private String isin;
    @Column(name = "symbol", unique = true, nullable = false)
    private String symbol;
    @Column(name = "name", nullable = false)
    private String name;

    protected FinancialProduct() {
    }

    public FinancialProduct(String isin, String symbol, String name) {
        this.isin = isin;
        this.symbol = symbol;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FinancialProduct{" +
                "id=" + id +
                ", isin='" + isin + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
