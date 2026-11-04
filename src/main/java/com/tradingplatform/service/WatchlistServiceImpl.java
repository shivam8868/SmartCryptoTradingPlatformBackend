package com.tradingplatform.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Watchlist;
import com.tradingplatform.repository.WatchlistRepository;

@Service
public class WatchlistServiceImpl implements WatchlistService{

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Override
    public Watchlist findUserWatchlist(Long userId) {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if(watchlist==null) {
            throw new RuntimeException("watchlist not found");
        }
        return watchlist;
    }

    @Override
    public Watchlist createWatchlist(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) {
        Optional<Watchlist> watchlistOptional=watchlistRepository.findById(id);
        if(watchlistOptional.isEmpty()) {
            throw new RuntimeException("Watchlist not found");
        }
        return watchlistOptional.get();
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, User user) {
        Watchlist watchlist=findUserWatchlist(user.getId());
        if(watchlist.getCoins().contains(coin)) {
            watchlist.getCoins().remove(coin);
        }
        else {
            watchlist.getCoins().add(coin);
            
        }
        watchlistRepository.save(watchlist);
        return coin;

    }

}
