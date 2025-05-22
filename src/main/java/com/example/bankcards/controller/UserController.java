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
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с данными пользователей
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Выдача админских прав пользователю
     *
     * @param username - имя пользователя
     * @return - информация о пользователе
     */
    @Operation(summary = "Сделать пользователя админом")
    @PostMapping("/make-admin")
    public ResponseEntity<UserDto> makeAdmin(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.makeAdmin(username));
    }

    /**
     * Лишение админских прав
     *
     * @param username - имя пользователя
     * @return - информацию о пользователе
     */
    @Operation(summary = "Отобрать права админа")
    @PostMapping("/takeaway-admin")
    public ResponseEntity<UserDto> takeawayAdmin(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.takeAwayAdmin(username));
    }

    /**
     * Удаление пользователя
     *
     * @param username - имя пользователя
     * @return - информация о пользователе
     */
    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/delete-user")
    public ResponseEntity<UserDto> deleteUser(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }

    /**
     * Получение информации о пользователе
     *
     * @param username - имя пользователя
     * @return - информация о пользователе
     */
    @Operation(summary = "Получить пользователя по имени")
    @GetMapping("/get")
    public ResponseEntity<UserDto> get(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(new UserDto(userService.getByUsername(username)));
    }

    /**
     * Получение списка всех пользователей
     *
     * @param page - номер страницы
     * @param size - размер страницы
     * @return - возвращает список пользователей
     */
    @Operation(summary = "Посмотреть список всех пользователей")
    @GetMapping("/get-all")
    public ResponseEntity<Page<UserDto>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAll(PageRequest.of(page, size)));
    }

}
