package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@Schema(description = "Перевод с одной карты на другую")
public class ChangeAmountDto {

    private String cardNumberFrom;
    private String cardNumberTo;
    private BigDecimal amount;
}
