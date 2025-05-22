package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями")
@SecurityRequirement(name = "BearerAuth")
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

    @Operation(summary = "Получить пользователя по имени")
    @GetMapping("/get")
    public ResponseEntity<UserDto> get(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(new UserDto(userService.getByUsername(username)));
    }

    @Operation(summary = "Посмотреть список всех пользователей")
    @GetMapping("/get-all")
    public ResponseEntity<Page<UserDto>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page, size)));
    }

}
