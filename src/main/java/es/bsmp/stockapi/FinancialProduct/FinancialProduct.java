package es.bsmp.stockapi.FinancialProduct;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "financial_product")
@Data
@NoArgsConstructor
public class FinancialProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(name = "isin", nullable = false)
    private String isin;
    @Column(name = "symbol", unique = true, nullable = false)
    private String symbol;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "data_source", nullable = false)
    @Enumerated(EnumType.STRING)
    private DataSource dataSource;
    @Column(name = "data_source_symbol",nullable = false)
    private String dataSourceSymbol;

    public FinancialProduct(String isin, String symbol, String name) {
        this.isin = isin;
        this.symbol = symbol;
        this.name = name;
    }

}
