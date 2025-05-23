package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CardService cardService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardController cardController;

    private final User testUser = User.builder()
            .username("testUser")
            .role(Role.USER)
            .build();
    private final Card testCard = Card.builder()
            .cardNumber("1234 1234 1234 1234")
            .cardStatus(CardStatus.ACTIVE)
            .balance(BigDecimal.valueOf(1000))
            .user(testUser)
            .build();
    private final CardFullInformation testFullCard = new CardFullInformation(testCard);
    private final CardBriefInformation testBriefCard = new CardBriefInformation(testCard);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .build();
    }

    /**
     * Получение карты по номеру, получение карты от админа
     *
     * @throws Exception - ошибка
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCardByCardNumber_ShouldReturnCard_ForAdmin() throws Exception {
        when(cardService.getByCardNumber(anyString())).thenReturn(testCard);

        mockMvc.perform(get("/api/card/get")
                        .param("cardNumber", "1234 1234 1234 1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").exists());
    }

    /**
     * Создание карты, создание от админа
     *
     * @throws Exception - ошибка
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_ShouldCreateCard_ForAdmin() throws Exception {
        when(cardService.createCard(anyString())).thenReturn(testFullCard);

        mockMvc.perform(post("/api/card/create")
                        .param("username", "testUser")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 1234"));
    }

    /**
     * Активация карты, только от админа
     *
     * @throws Exception - ошибка
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_ShouldActivateCard_ForAdmin() throws Exception {
        when(cardService.activateCard(anyString())).thenReturn(testFullCard);

        mockMvc.perform(post("/api/card/activate")
                        .param("cardName", "1234 5678 9012 3456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));
    }

    /**
     * Блокировка карты, только от админа
     *
     * @throws Exception - ошибка
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_ShouldBlockCard_ForAdmin() throws Exception {
        testCard.setCardStatus(CardStatus.BLOCKED);
        when(cardService.blockCard(anyString())).thenReturn(testFullCard);

        mockMvc.perform(post("/api/card/block")
                        .param("cardName", "1234 5678 9012 3456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));
    }

    /**
     * Удаление карты
     *
     * @throws Exception - ошибка
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_ShouldDeleteCard_ForAdmin() throws Exception {
        when(cardService.deleteCard(anyString())).thenReturn(testFullCard);

        mockMvc.perform(delete("/api/card/delete")
                        .param("cardName", "1234 5678 9012 3456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 1234"));
    }

    /**
     * Получение баланса, получение баланса
     *
     * @throws Exception
     */
    @Test
    void getBalance_ShouldReturnCardBalance() throws Exception {
        when(cardService.getCardBalance(anyString())).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/card/balance")
                        .param("cardNumber", "1234 1234 1234 1234"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    /**
     * Перевод между картами
     *
     * @throws Exception - ошибка
     */
    @Test
    void transfer_ShouldPerformTransfer() throws Exception {
        ChangeAmountDto transferDto = new ChangeAmountDto();
        transferDto.setCardNumberFrom("1234 5678 9012 3456");
        transferDto.setCardNumberTo("9876 5432 1098 7654");
        transferDto.setAmount(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    /**
     * Запрос на блокировку карты
     *
     * @throws Exception - ошибка
     */
    @Test
    void requestToBlock_ShouldCreateRequest() throws Exception {
        BlockCardRequestDto requestDto = new BlockCardRequestDto();
        requestDto.setCardNumber("1234 5678 9012 3456");
        requestDto.setReason("Lost card");

        BlockCardRequestInfo response = new BlockCardRequestInfo();
        response.setCardNumber("1234 5678 9012 3456");
        response.setReason("Lost card");

        when(cardService.requestToBlockCard(any(BlockCardRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/card/request-to-block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));
    }

}