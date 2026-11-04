package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.Coin;

public interface CoinRepository extends JpaRepository<Coin, String> {

}
