package com.example.bankcards.handler;


import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.exception.BlockedRequestException;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Перехватчик пользовательских ошибок
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок в случае неправильного ввода аргументов
     *
     * @param e       - сообщение об ошибке
     * @param request - запрос
     * @param locale  - информация о местонахождении
     * @return - возвращает информацию об ошибке
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
     * Обработка ошибок связанных с работой с пользователями
     *
     * @param e       - сообщение об ошибке
     * @param request - запрос
     * @param locale  - информация о местонахождении
     * @return - возвращает информацию об ошибке
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

    /**
     * Обработка ошибок при работе с картами
     *
     * @param e       - сообщение об ошибке
     * @param request - запрос
     * @param locale  - информация о местонахождении
     * @return - возвращает информацию об ошибке
     */
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

    /**
     * Обработка ошибок при запросах на блокировку карты
     *
     * @param e       - сообщение об ошибке
     * @param request - запрос
     * @param locale  - информация о местонахождении
     * @return - возвращает информацию об ошибке
     */
    @ExceptionHandler(BlockedRequestException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            BlockedRequestException e,
            WebRequest request,
            Locale locale) {
        log.warn("Ошибка при запросе на блокировку карты {}", e.getMessage());

        Map<String, String> errors = getExceptionInfo(e);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка при запросе на блокировку карты",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка всех ошибок
     *
     * @param e       - сообщение об ошибке
     * @param request - запрос
     * @param locale  - информация о местонахождении
     * @return - возвращает информацию об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(
            Exception e,
            WebRequest request,
            Locale locale) {
        log.warn("Ошибка {}", e.getMessage());

        Map<String, String> errors = getExceptionInfo(e);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Получение сообщения об ошибках
     *
     * @param e - сообщение об ошибке
     * @return - возвращает сообщение об ошибках
     */
    public Map<String, String> getExceptionInfo(Exception e) {
        return new HashMap<>() {{
            put("Сообщение об ошибке", e.getMessage());
        }};
    }

}
