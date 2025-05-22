package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockCardRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для создания запросов на блокировку
 */
public interface BlockCardRequestRepository extends JpaRepository<BlockCardRequest, Long> {

    boolean existsByCard_CardNumber(String cardNumber);

    BlockCardRequest findByCard_CardNumber(String cardNumber);
}