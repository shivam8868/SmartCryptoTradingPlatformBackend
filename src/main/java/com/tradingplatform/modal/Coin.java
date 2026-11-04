package com.tradingplatform.modal;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
@Entity
@Data
public class Coin {
    // all long and double changed to Long and Double
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("current_price")
    private Double currentPrice; //c

    @JsonProperty("market_cap")
    private Long marketCap;//c

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;//c

    @JsonProperty("fully_diluted_valuation")
    private Long fullyDilutedValuation;//c

    @JsonProperty("total_volume")
    private Long totalVolume;//c

    @JsonProperty("high_24h")
    private Double high24h;//c

    @JsonProperty("low_24h")
    private Double low24h;

    @JsonProperty("price_change_24h")
    private Double priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;

    @JsonProperty("market_cap_change_24h")
    private Long marketCapChange24h;

    @JsonProperty("market_cap_change_percentage_24h")
    private Long marketCapChangePercentage24h;

    @JsonProperty("circulating_supply")
    private Long circulatingSupply;

    @JsonProperty("total_supply")
    private Long totalSupply;

    @JsonProperty("max_supply")
    private Long maxSupply;

    @JsonProperty("ath")
    @Column(nullable = true)
    private Double ath;

    @JsonProperty("ath_change_percentage")
    private Double athChangePercentage;

    @JsonProperty("ath_date")
    private Date athDate;

    @JsonProperty("atl")
    private Double atl;

    @JsonProperty("atl_change_percentage")
    private Double atlChangePercentage;

    @JsonProperty("atl_date")
    private Date atlDate;

    @JsonProperty("roi")
    @JsonIgnore
    private String roi;

    @JsonProperty("last_updated")
    private Date lastUpdated;







}
