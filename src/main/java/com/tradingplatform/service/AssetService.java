package com.tradingplatform.service;

import java.util.List;

import com.tradingplatform.modal.Asset;
import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.User;

public interface AssetService {
    Asset createAsset(User user, Coin coin, double quantity);
    
    Asset getAssetById(Long assetId);

    Asset getAssetByUserIdAndId(Long userId, Long assetId);

    List<Asset> getUserAssets(Long userId);

    Asset updateAsset(Long assetId, double quantity);

    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);

    void deleteAsset(Long assetId);
}
