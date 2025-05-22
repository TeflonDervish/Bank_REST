package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с картами
 */
public interface CardRepository extends CrudRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    Page<Card> findByUser(User user, Pageable pageable);

    Page<Card> findAll(Pageable pageable);
}