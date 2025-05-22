package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Dto для выдачи информации о пользователе
 */
@Data
@Schema(description = "Информация о пользователе")
public class UserDto {

    public UserDto(User user) {
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    @Schema(example = "Vyacheslav")
    private String username;

    @Schema(example = "USER")
    private Role role;

}
