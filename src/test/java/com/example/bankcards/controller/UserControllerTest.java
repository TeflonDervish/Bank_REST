package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Класс для проверки работы UserController
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final User user = User.builder()
            .username("testUser")
            .role(Role.ADMIN)
            .build();

    private final UserDto testUserDto = new UserDto(user);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    /**
     * Выдача админских прав, и получение юзера
     *
     * @throws Exception - ошибка
     */
    @Test
    void makeAdmin_ShouldReturnUpdatedUser() throws Exception {
        UserDto adminUserDto = new UserDto();
        adminUserDto.setUsername("testUser");
        adminUserDto.setRole(Role.ADMIN);

        when(userService.makeAdmin("testUser")).thenReturn(adminUserDto);

        mockMvc.perform(post("/api/user/make-admin")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    /**
     * Лишение админских прав
     *
     * @throws Exception - ошибка
     */
    @Test
    void takeawayAdmin_ShouldReturnUpdatedUser() throws Exception {
        testUserDto.setRole(Role.ADMIN);
        when(userService.takeAwayAdmin("testUser")).thenReturn(testUserDto);

        mockMvc.perform(post("/api/user/takeaway-admin")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    /**
     * Удаление пользователя
     *
     * @throws Exception - ошибка
     */
    @Test
    void deleteUser_ShouldReturnDeletedUser() throws Exception {
        when(userService.deleteUser("testUser")).thenReturn(testUserDto);

        mockMvc.perform(delete("/api/user/delete-user")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    /**
     * Получение пользователя по имени
     *
     * @throws Exception
     */
    @Test
    void get_ShouldReturnUser() throws Exception {
        when(userService.getByUsername("testUser")).thenReturn(user);

        mockMvc.perform(get("/api/user/get")
                        .param("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    /**
     * Получение всех пользователей, получение списка
     *
     * @throws Exception - ошибка
     */
    @Test
    void getAllUser_ShouldReturnPageOfUsers() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<UserDto> users = Collections.singletonList(testUserDto);
        Page<UserDto> page = new PageImpl<>(users, pageRequest, 1);

        when(userService.getAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/user/get-all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    /**
     * Получение всех пользователей, возвращает список пользователей
     *
     * @throws Exception - ошибка
     */
    @Test
    void getAllUser_WithDefaultPagination_ShouldUseDefaults() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<UserDto> users = Collections.singletonList(testUserDto);
        Page<UserDto> page = new PageImpl<>(users, pageRequest, 1);

        when(userService.getAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/user/get-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testUser"));
    }
}