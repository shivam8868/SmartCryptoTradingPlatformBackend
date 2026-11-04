package com.tradingplatform.service;

import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId);
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id);

    Coin addItemToWatchlist(Coin coin, User user);
}
