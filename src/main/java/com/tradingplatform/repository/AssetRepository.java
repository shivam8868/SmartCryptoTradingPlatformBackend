package com.tradingplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUser_Id(Long userId);
    
    Asset findByUser_IdAndCoin_Id(Long userId, String coinId);

}
