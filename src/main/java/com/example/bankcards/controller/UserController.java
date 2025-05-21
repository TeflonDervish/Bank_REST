package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Сделать пользователя админом")
    @PostMapping("/make-admin")
    public ResponseEntity<UserDto> makeAdmin(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.makeAdmin(username));
    }

    @Operation(summary = "Отобрать права админа")
    @PostMapping("/takeaway-admin")
    public ResponseEntity<UserDto> takeawayAdmin(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.takeAwayAdmin(username));
    }

    @Operation(summary = "Удалить пользователя")
    @PostMapping("/delete-user")
    public ResponseEntity<UserDto> deleteUser(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }
}
