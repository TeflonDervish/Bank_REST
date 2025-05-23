package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.UserException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Проверка работы класса UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /**
     * Создание пользователя, успешное создание
     */
    @Test
    void createUser_ShouldSaveNewUser() {
        User newUser = User.builder()
                .username("newuser")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).save(newUser);
    }

    /**
     * Проверка создания пользователя, ожидается выброс ошибки
     */
    @Test
    void createUser_ShouldThrowExceptionWhenUserExists() {
        User existingUser = User.builder()
                .username("existing")
                .build();

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(UserException.class, () -> userService.createUser(existingUser));
        verify(userRepository).existsByUsername("existing");
        verify(userRepository, never()).save(any());
    }

    /**
     * Получение пользователя, успешное выполнение
     */
    @Test
    void getByUsername_ShouldReturnUser() {
        User expectedUser = User.builder()
                .username("testuser")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(expectedUser));

        User result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    /**
     * Получение пользователя, ошибка при получении несуществующего пользователя
     */
    @Test
    void getByUsername_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getByUsername("unknown"));
        verify(userRepository).findByUsername("unknown");
    }

    /**
     * Получение списка пользователей, успешное выполнение
     */
    @Test
    void getAll_ShouldReturnPageOfUsers() {
        Pageable pageable = mock(Pageable.class);
        User user = User.builder()
                .username("testuser")
                .build();
        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDto> result = userService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    /**
     * Удаление пользователя, успешное выполнение
     */
    @Test
    void deleteUser_ShouldDeleteAndReturnDto() {
        User user = User.builder()
                .username("todelete")
                .build();

        when(userRepository.findByUsername("todelete")).thenReturn(Optional.of(user));

        UserDto result = userService.deleteUser("todelete");

        assertEquals("todelete", result.getUsername());
        verify(userRepository).findByUsername("todelete");
        verify(userRepository).delete(user);
    }

    /**
     * Выдача админских прав, успешное выполнение
     */
    @Test
    void makeAdmin_ShouldUpdateRoleToAdmin() {
        User user = User.builder()
                .username("user")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.makeAdmin("user");

        assertEquals(Role.ADMIN, user.getRole());
        assertEquals("user", result.getUsername());
        verify(userRepository).findByUsername("user");
        verify(userRepository).save(user);
    }

    /**
     * Лишение админских прав, успешное выполнение
     */
    @Test
    void takeAwayAdmin_ShouldUpdateRoleToUser() {
        User admin = User.builder()
                .username("admin")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(userRepository.save(admin)).thenReturn(admin);

        UserDto result = userService.takeAwayAdmin("admin");

        assertEquals(Role.USER, admin.getRole());
        assertEquals("admin", result.getUsername());
        verify(userRepository).findByUsername("admin");
        verify(userRepository).save(admin);
    }

    /**
     * Получение текущего пользователя, успешное выполнение
     */
    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        User expectedUser = User.builder()
                .username("current")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("current");
        when(userRepository.findByUsername("current")).thenReturn(Optional.of(expectedUser));

        User result = userService.getCurrentUser();

        assertEquals("current", result.getUsername());
        verify(securityContext).getAuthentication();
        verify(authentication).getName();
        verify(userRepository).findByUsername("current");
    }

    /**
     * Проверка существования пользователя, успешное выполнение
     */
    @Test
    void existsByUsername_ShouldReturnTrueWhenUserExists() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        boolean result = userService.existsByUsername("existing");

        assertTrue(result);
        verify(userRepository).existsByUsername("existing");
    }

    /**
     * Проверка существования пользователя, успешное выполнение
     */
    @Test
    void existsByUsername_ShouldReturnFalseWhenUserNotExists() {
        when(userRepository.existsByUsername("nonexisting")).thenReturn(false);

        boolean result = userService.existsByUsername("nonexisting");

        assertFalse(result);
        verify(userRepository).existsByUsername("nonexisting");
    }
}