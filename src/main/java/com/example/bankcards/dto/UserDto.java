package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о пользователе")
public class UserDto {

    public UserDto(User user) {
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    private String username;
    private Role role;

}
