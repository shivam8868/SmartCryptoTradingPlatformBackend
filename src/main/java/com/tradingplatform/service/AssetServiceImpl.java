package com.tradingplatform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.modal.Asset;
import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.User;
import com.tradingplatform.repository.AssetRepository;

@Service
public class AssetServiceImpl implements AssetService {


    @Autowired
    private AssetRepository assetRepository;
    @Override
    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());
        return assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long assetId) {
        return assetRepository.findById(assetId).orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    @Override
    public Asset getAssetByUserIdAndId(Long userId, Long assetId) {
        return null;
    }

    @Override
    public List<Asset> getUserAssets(Long userId) {
        return assetRepository.findByUser_Id(userId);
    }

    @Override
    public Asset updateAsset(Long assetId, double quantity) {
        Asset oldAsset = getAssetById(assetId);
        oldAsset.setQuantity(quantity+oldAsset.getQuantity());
        return assetRepository.save(oldAsset);
    }

    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
        return assetRepository.findByUser_IdAndCoin_Id(userId, coinId);
    }

    @Override
    public void deleteAsset(Long assetId) {
        assetRepository.deleteById(assetId);
    }

}
