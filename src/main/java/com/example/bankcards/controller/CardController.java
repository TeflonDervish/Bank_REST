package com.example.bankcards.controller;

import com.example.bankcards.dto.CardBriefInformation;
import com.example.bankcards.dto.CardFullInformation;
import com.example.bankcards.dto.ChangeAmountDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/card")
@Tag(name = "Работа с картами")
@SecurityRequirement(name = "BearerAuth")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping("/get")
    @Operation(summary = "Получение карты по номеру")
    public ResponseEntity<CardFullInformation> getCardByCardNumber(
            @RequestParam(defaultValue = "1234 1234 1234 1234") String cardNumber) {
        Card card = cardService.getByCardNumber(cardNumber);
        return ResponseEntity.ok(new CardFullInformation(card));
    }

    @GetMapping("/get-all")
    @Operation(summary = "Получение списка всех карт")
    public ResponseEntity<Page<CardBriefInformation>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page, size)));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Создание карты")
    public ResponseEntity<CardFullInformation> createCard(
            @RequestParam String username) {
        return ResponseEntity.ok(cardService.createCard(username));
    }

    @PostMapping("/activate")
    @Operation(summary = "Активация карты")
    public ResponseEntity<CardFullInformation> activateCard(
            @RequestParam(
                    defaultValue = "1234 1234 1234 1234")
            String cardName) {
        return ResponseEntity.ok(cardService.activateCard(cardName));
    }

    @PostMapping("/block")
    @Operation(summary = "Блокировка карты")
    public ResponseEntity<CardFullInformation> blockCard(
            @RequestParam(
                    defaultValue = "1234 1234 1234 1234")
            String cardName) {
        return ResponseEntity.ok(cardService.blockCard(cardName));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление карты карты")
    public ResponseEntity<CardFullInformation> deleteCard(
            @RequestParam(
                    defaultValue = "1234 1234 1234 1234")
            String cardName) {
        return ResponseEntity.ok(cardService.deleteCard(cardName));
    }

    @GetMapping("/get-my-cards")
    @Operation(summary = "Получение списка всех карт текущего пользователя")
    public ResponseEntity<Page<CardBriefInformation>> getAllMyCard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(cardService.getUsersCard(user.getUsername(), PageRequest.of(page, size)));
    }

    @GetMapping("/get-user_card")
    @Operation(summary = "Получение списка карт пользователя")
    public ResponseEntity<Page<CardBriefInformation>> getAllUserCard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "Имя пользователя") String username) {
        return ResponseEntity.ok(cardService.getUsersCard(username, PageRequest.of(page, size)));
    }

    @GetMapping("/balance")
    @Operation(summary = "Получение баланса карты")
    public ResponseEntity<BigDecimal> getBalance(
            @RequestParam(defaultValue = "1234 1234 1234 1234") String cardNumber) {
        return ResponseEntity.ok(cardService.getCardBalance(cardNumber));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод денег с карты")
    public ResponseEntity<Void> transfer(
            @RequestBody @Valid ChangeAmountDto transfer) {
        cardService.cardToCardTransfer(transfer);
        return ResponseEntity.ok().build();
    }

}
