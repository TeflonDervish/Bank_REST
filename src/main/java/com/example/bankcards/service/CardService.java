package com.example.bankcards.service;

import com.example.bankcards.dto.CardBriefInformation;
import com.example.bankcards.dto.CardFullInformation;
import com.example.bankcards.dto.ChangeAmountDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberGenerate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

/**
 * Сервис для работы с данными карт
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private static final Integer CARD_EXPIRATION = 5;
    private static final Log log = LogFactory.getLog(CardService.class);

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardNumberGenerate cardNumberGenerate;

    /**
     * Получение карты по номеру
     *
     * @param cardNumber - номер карты
     * @return - возвращает карту
     */
    public Card getByCardNumber(String cardNumber) {
        log.info("Получение карты по номеру");
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardException("Карты с номером " + cardNumber + " не существует"));
        User user = userService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN)) {
            log.info("Карта получена под админом");
            return card;
        }

        if (card.getCardStatus().equals(CardStatus.BLOCKED)) {
            log.info("Попытка получить заблокированную карту");
            throw new CardException("Карта заблокирована");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            log.info("У карты вышел срок годности");
            card.setCardStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        }

        if (card.getCardStatus().equals(CardStatus.EXPIRED)) {
            log.info("Попытка получить просроченную карту");
            throw new CardException("Карта просрочена");
        }

        return card;
    }

    /**
     * Создание карты
     *
     * @param username - имя пользователя
     * @return - возвращает промежуточную информацию о карте
     */
    @Transactional
    public CardFullInformation createCard(String username) {
        log.info("Создание карты");
        User user = userService.getByUsername(username);
        Card card = Card.builder()
                .cardNumber(cardNumberGenerate.generateCardNumber())
                .cardStatus(CardStatus.ACTIVE)
                .user(user)
                .expirationDate(LocalDate.now().plusYears(CARD_EXPIRATION))
                .balance(BigDecimal.ZERO)
                .build();
        cardRepository.save(card);
        return new CardFullInformation(card);
    }

    /**
     * Проверка прав доступа пользователя
     *
     * @param cardNumber - номер карты
     */
    public void isCanGetCardAccess(String cardNumber) {
        log.info("Проверка доступа к карте");
        User user = userService.getCurrentUser();
        Card card = getByCardNumber(cardNumber);
        if (!((user.getRole().equals(Role.ADMIN)) || (card.getUser().getId().equals(user.getId()))))
            throw new CardException("У вас нет доступа к этой карте");
    }

    /**
     * Активация карты
     *
     * @param cardNumber - номер карты
     * @return - возвращает подробную информацию о карте
     */
    public CardFullInformation activateCard(String cardNumber) {
        log.info("Карты активирована");
        Card card = getByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return new CardFullInformation(card);
    }

    /**
     * Блокировка карты
     *
     * @param cardNumber - номер карты
     * @return - возвращает подробную информацию о карте
     */
    public CardFullInformation blockCard(String cardNumber) {
        log.info("Карта заблокирована");
        Card card = getByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return new CardFullInformation(card);
    }

    /**
     * Удаление карты
     *
     * @param cardNumber - номер карты
     * @return - возвращает подробную информацию о карте
     */
    public CardFullInformation deleteCard(String cardNumber) {
        log.info("Карта удалена");
        Card card = getByCardNumber(cardNumber);
        cardRepository.delete(card);
        return new CardFullInformation(card);
    }

    /**
     * Получение списка пользовательских карты
     *
     * @param username - имя пользователя
     * @param pageable - пагинация
     * @return - возвращает список с краткой информацией о карте
     */
    public Page<CardBriefInformation> getUsersCard(String username, Pageable pageable) {
        log.info("Получен список карта пользователя " + username);
        User user = userService.getByUsername(username);
        return cardRepository.findByUser(user, pageable)
                .map(CardBriefInformation::new);
    }

    /**
     * Получение списка всех карта
     *
     * @param pageable - пагинация
     * @return - возвращает список с краткой информацией о картах
     */
    public Page<CardBriefInformation> getAllCards(Pageable pageable) {
        log.info("Получение всех карт");
        return cardRepository.findAll(pageable)
                .map(CardBriefInformation::new);
    }

    /**
     * Получение баланса карты
     *
     * @param cardNumber - номер карты
     * @return - баланс карты
     */
    public BigDecimal getCardBalance(String cardNumber) {
        log.info("Получение баланса карты");
        isCanGetCardAccess(cardNumber);
        return getByCardNumber(cardNumber)
                .getBalance();
    }

    /**
     * Перевод денег с одной карты на другую
     *
     * @param changeAmountDto - dto на изменение баланса
     */
    @Transactional
    public void cardToCardTransfer(ChangeAmountDto changeAmountDto) {
        log.info("Перевод между картами");
        Card cardFrom = getByCardNumber(changeAmountDto.getCardNumberFrom());
        Card cardTo = getByCardNumber(changeAmountDto.getCardNumberTo());

        isCanGetCardAccess(changeAmountDto.getCardNumberFrom());
        isCanGetCardAccess(changeAmountDto.getCardNumberTo());

        if (!cardFrom.getUser().getId().equals(cardTo.getUser().getId()))
            throw new CardException("Можно переводить деньги только в рамках карт одного пользователя, " +
                    "потому что так сказано в ТЗ)");

        if (cardFrom.getBalance().subtract(changeAmountDto.getAmount()).compareTo(BigDecimal.ZERO) < 0)
            throw new CardException("На счете недостаточно средств");

        cardFrom.changeBalance(changeAmountDto.getAmount().negate());
        cardTo.changeBalance(changeAmountDto.getAmount());
        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);
    }


}
