package br.com.workmade.ingestdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Integer id;
    private String name;
    private BigDecimal price;
    private String barCode;
    private String description;
    private String imageUrl;
    private ProductStockDTO productStock;
}
