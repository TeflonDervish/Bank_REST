package com.example.bankcards.dto;

import com.example.bankcards.entity.BlockCardRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "формат запроса на блокировку")
public class BlockCardRequestDto {

    @Schema(description = "Номер карты", defaultValue = "1234 1234 1234 1234")
    @Size(min = 19, max = 19, message = "Номер карты должен содержать 19 символов (вместе с пробелами)")
    @NotBlank(message = "Номер карты не может быть пустым")
    private String cardNumber;

    @Schema(description = "Причина запроса блокировки", defaultValue = "Потому что")
    @NotBlank(message = "Причина блокировки не может быть пустой")
    private String reason;
}
