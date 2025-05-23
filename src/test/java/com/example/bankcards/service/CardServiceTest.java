package com.example.bankcards.service;

import com.example.bankcards.dto.ChangeAmountDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.repository.BlockCardRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberGenerate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Класс для проверки класс CardService
 */
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BlockCardRequestRepository bacRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardNumberGenerate cardNumberGenerate;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private AutoCloseable closeable;

    /**
     * Тестовый пользователь
     */
    private final User testUser = User.builder()
            .id(1L)
            .username("testuser")
            .role(Role.USER)
            .build();

    /**
     * Тестовая карта
     */
    private final Card testCard = Card.builder()
            .id(1L)
            .cardNumber("1234 5678 9101 1121")
            .cardStatus(CardStatus.ACTIVE)
            .balance(BigDecimal.valueOf(1000))
            .user(testUser)
            .expirationDate(LocalDate.now().plusYears(1))
            .build();

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * Получение карты по номеру, успешное выполнение
     */
    @Test
    void testGetByCardNumber_Success() {
        when(cardRepository.findByCardNumber(testCard.getCardNumber()))
                .thenReturn(Optional.of(testCard));
        when(userService.getCurrentUser()).thenReturn(testUser);

        Card card = cardService.getByCardNumber(testCard.getCardNumber());

        assertEquals(testCard, card);
    }

    /**
     * Получение карты по номеру, карта просрочена
     */
    @Test
    void testGetByCardNumber_ExpiredCardThrowsException() {
        Card expiredCard = Card.builder()
                .cardNumber("expired")
                .expirationDate(LocalDate.now().minusDays(1))
                .cardStatus(CardStatus.ACTIVE)
                .user(testUser)
                .build();

        when(cardRepository.findByCardNumber("expired")).thenReturn(Optional.of(expiredCard));
        when(userService.getCurrentUser()).thenReturn(testUser);

        assertThrows(CardException.class, () -> cardService.getByCardNumber("expired"));
    }

    /**
     * Создание карты, успешное выполнение
     */
    @Test
    void testCreateCard_Success() {
        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(cardNumberGenerate.generateCardNumber()).thenReturn("1234 1234 1234 1234");

        cardService.createCard("testuser");

        verify(cardRepository).save(cardCaptor.capture());
        assertEquals("**** **** **** 1234", cardCaptor.getValue().getCardNumber());
        assertEquals(BigDecimal.ZERO, cardCaptor.getValue().getBalance());
    }

    /**
     * Получение баланса карты, успешное выполнение
     */
    @Test
    void testGetCardBalance_Success() {
        when(cardRepository.findByCardNumber(testCard.getCardNumber()))
                .thenReturn(Optional.of(testCard));
        when(userService.getCurrentUser()).thenReturn(testUser);

        BigDecimal balance = cardService.getCardBalance(testCard.getCardNumber());

        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    /**
     * Перевод между картами, успешное выполнение
     */
    @Test
    void testCardToCardTransfer_Success() {
        Card from = testCard;
        Card to = Card.builder()
                .cardNumber("2222 3333 4444 5555")
                .balance(BigDecimal.valueOf(500))
                .user(testUser)
                .cardStatus(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusYears(1))
                .build();

        ChangeAmountDto dto = new ChangeAmountDto(
                from.getCardNumber(),
                to.getCardNumber(),
                BigDecimal.valueOf(100)
        );

        when(cardRepository.findByCardNumber(from.getCardNumber())).thenReturn(Optional.of(from));
        when(cardRepository.findByCardNumber(to.getCardNumber())).thenReturn(Optional.of(to));
        when(userService.getCurrentUser()).thenReturn(testUser);

        cardService.cardToCardTransfer(dto);

        assertEquals(BigDecimal.valueOf(900), from.getBalance());
        assertEquals(BigDecimal.valueOf(600), to.getBalance());
    }

    /**
     * Перевод между картами, недостаточно средств
     */
    @Test
    void testCardToCardTransfer_InsufficientFunds() {
        ChangeAmountDto dto = new ChangeAmountDto(
                testCard.getCardNumber(),
                "receiver",
                BigDecimal.valueOf(5000)
        );

        Card toCard = Card.builder()
                .cardNumber("receiver")
                .user(testUser)
                .balance(BigDecimal.valueOf(100))
                .cardStatus(CardStatus.ACTIVE)
                .expirationDate(LocalDate.now().plusYears(1))
                .build();

        when(cardRepository.findByCardNumber(testCard.getCardNumber()))
                .thenReturn(Optional.of(testCard));
        when(cardRepository.findByCardNumber("receiver"))
                .thenReturn(Optional.of(toCard));
        when(userService.getCurrentUser()).thenReturn(testUser);

        assertThrows(CardException.class, () -> cardService.cardToCardTransfer(dto));
    }

    /**
     * Получение карт пользователя, успешное выполнение
     */
    @Test
    void testGetUsersCard_ReturnsCardPage() {
        when(userService.getByUsername("testuser")).thenReturn(testUser);
        when(cardRepository.findByUser(eq(testUser), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(testCard)));

        var page = cardService.getUsersCard("testuser", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals(testCard.getCardNumber(), page.getContent().get(0).getCardNumber());
    }

    /**
     * Блокировка карты, успешное выполнение
     */
    @Test
    void testBlockCard_UpdatesStatusToBlocked() {
        when(cardRepository.findByCardNumber(testCard.getCardNumber()))
                .thenReturn(Optional.of(testCard));
        when(userService.getCurrentUser()).thenReturn(testUser);

        cardService.blockCard(testCard.getCardNumber());

        verify(cardRepository).save(cardCaptor.capture());
        assertEquals(CardStatus.BLOCKED, cardCaptor.getValue().getCardStatus());
    }

}
