package br.com.workmade.ingestdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVFileDTO {

    private String name;
    private String barCode;
    private String description;
    private BigDecimal price;
}
