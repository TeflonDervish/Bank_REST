package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.UserException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Log log = LogFactory.getLog(UserService.class);
    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User createUser(User user) {
        log.info("Создание пользователя");
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserException("Пользователь с таким именем уже существует");
        }
        return save(user);
    }

    public User getByUsername(String username) {
        log.info("Получение пользователя " + username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("Пользователя с такими именем не существует"));
    }

    public Page<UserDto> getAll(Pageable pageable) {
        log.info("Получение информации о всех пользователях");
        return userRepository.findAll(pageable)
                .map(UserDto::new);
    }

    public UserDto deleteUser(String username) {
        log.info("Удаление пользователя" + username);
        User user = getByUsername(username);
        userRepository.delete(user);
        return new UserDto(user);
    }

    public UserDto makeAdmin(String username) {
        log.info("Пользователь " + username + " получил админские права");
        User user = getByUsername(username);
        user.setRole(Role.ADMIN);
        return new UserDto(save(user));
    }

    public UserDto takeAwayAdmin(String username) {
        log.info("Пользователь " + username + " лишился админских прав" );
        User user = getByUsername(username);
        user.setRole(Role.USER);
        return new UserDto(save(user));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        log.info("Получение информации о текущем пользователе");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
