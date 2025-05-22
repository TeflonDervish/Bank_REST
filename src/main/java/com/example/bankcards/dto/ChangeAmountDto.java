package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@Schema(description = "Перевод с одной карты на другую")
public class ChangeAmountDto {

    @Schema(description = "Карта с который происходит перевод", defaultValue = "1234 1234 1234 1234")
    @Size(min = 19, max = 19, message = "Номер карты должен содержать 19 символов (вместе с пробелами)")
    @NotBlank(message = "Номер карты не может быть пустым")
    private String cardNumberFrom;

    @Schema(description = "Карта на которую происходит перевод", defaultValue = "1234 1234 1234 1234")
    @Size(min = 19, max = 19, message = "Номер карты должен содержать 19 символов (вместе с пробелами)")
    @NotBlank(message = "Номер карты не может быть пустым")
    private String cardNumberTo;

    @Schema(description = "Сумма перевода", defaultValue = "0")
    @Size(min = 0, message = "Нельзя перевести меньше нуля")
    @NotBlank(message = "Сумма перевода не может быть отрицательной")
    private BigDecimal amount;
}
