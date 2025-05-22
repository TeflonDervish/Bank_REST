package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dto для выдачи токена
 */
@Data
@Builder
@Schema(description = "Ответ с токеном доступа")
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse implements Serializable {

    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;
}
