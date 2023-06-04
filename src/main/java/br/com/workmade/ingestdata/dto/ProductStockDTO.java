package br.com.workmade.ingestdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStockDTO {

    private Long id;

    private int quantity;

    private int buyCount;

    private StoreDTO store;
}
