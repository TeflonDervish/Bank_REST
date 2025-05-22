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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final Integer CARD_EXPIRATION = 5;

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardNumberGenerate cardNumberGenerate;

    public Card getByCardNumber(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardException("Карты с таким номером не существует"));
        User user = userService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN))
            return card;

        if (card.getCardStatus().equals(CardStatus.BLOCKED))
            throw new CardException("Карта заблокирована");

        if (card.getExpirationDate().isAfter(LocalDate.now()))
            card.setCardStatus(CardStatus.EXPIRED);

        if (card.getCardStatus().equals(CardStatus.EXPIRED))
            throw new CardException("Карта просрочена");

        return card;
    }

    @Transactional
    public CardFullInformation createCard(String username) {
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

    public CardFullInformation activateCard(String cardNumber) {
        Card card = getByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return new CardFullInformation(card);
    }

    public CardFullInformation blockCard(String cardNumber) {
        Card card = getByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return new CardFullInformation(card);
    }

    public CardFullInformation deleteCard(String cardNumber) {
        Card card = getByCardNumber(cardNumber);
        cardRepository.delete(card);
        return new CardFullInformation(card);
    }

    public Page<CardBriefInformation> getUsersCard(String username, Pageable pageable) {
        User user = userService.getByUsername(username);
        return cardRepository.findByUser(user, pageable)
                .map(CardBriefInformation::new);
    }

    public Page<CardBriefInformation> getAllCards(Pageable pageable) throws AccessDeniedException {
        if (!userService.getCurrentUser().getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("У вас нет доступа к этой опреации");
        return cardRepository.findAll(pageable)
                .map(CardBriefInformation::new);
    }

    public BigDecimal getCardBalance(String cardNumber) {
        User user = userService.getCurrentUser();
        Card card = getByCardNumber(cardNumber);

        if (!(user.getRole().equals(Role.ADMIN) || card.getUser().equals(user)))
            throw new CardException("У вас нет доступа к этой карте");

        return getByCardNumber(cardNumber)
                .getBalance();
    }

    @Transactional
    public void cardToCardTransfer(ChangeAmountDto changeAmountDto) {
        Card cardFrom = getByCardNumber(changeAmountDto.getCardNumberFrom());
        Card cardTo = getByCardNumber(changeAmountDto.getCardNumberTo());
        User user = userService.getCurrentUser();

        if (!(cardFrom.getUser().getId().equals(user.getId()) &&
                cardTo.getUser().getId().equals(user.getId()) ||
                user.getRole().equals(Role.ADMIN)))
            throw new CardException("У вас нет доступа к этим картам");

        if (!cardFrom.getUser().getId().equals(cardTo.getUser().getId()))
            throw new CardException("Можно переводить деньги только в рамках карт одного пользователя, " +
                    "потому что так сказано в ТЗ)");

        if (cardFrom.getBalance().subtract(changeAmountDto.getAmount()).compareTo(BigDecimal.ZERO) < 0)
            throw new CardException("На счете недостаточно средств");

        cardFrom.changeBalance(changeAmountDto.getAmount().negate());
        cardFrom.changeBalance(changeAmountDto.getAmount());
        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);
    }


}
