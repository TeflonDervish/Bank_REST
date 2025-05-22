package com.example.bankcards.controller;


import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок в случае неправильного ввода аргументов
     *
     * @param e
     * @param request
     * @param locale
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            WebRequest request,
            Locale locale) {
        log.warn("Ошибка валидации {}", e.getMessage());

        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param e
     * @param request
     * @param locale
     * @return
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            UserException e,
            WebRequest request,
            Locale locale) {
        log.warn("Ошибка при работе с пользователем {}", e.getMessage());

        Map<String, String> errors = getExceptionInfo(e);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка при работе с пользователем ",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            CardException e,
            WebRequest request,
            Locale locale) {
        log.warn("Ошибка при работе с картами {}", e.getMessage());

        Map<String, String> errors = getExceptionInfo(e);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка при работе с картой",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            AccessDeniedException e,
            WebRequest request,
            Locale locale) {
        log.warn("Отказано в доступе {}", e.getMessage());

        Map<String, String> errors = getExceptionInfo(e);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Отказано в доступе",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    public Map<String, String> getExceptionInfo(Exception e) {
        return new HashMap<>() {{
            put("Сообщение об ошибке", e.getMessage());
        }};
    }

}
