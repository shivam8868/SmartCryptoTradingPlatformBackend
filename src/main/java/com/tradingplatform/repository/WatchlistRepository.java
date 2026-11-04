package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.Watchlist;

public interface WatchlistRepository extends JpaRepository<Watchlist,Long>{

    Watchlist findByUserId(Long userId);
}
