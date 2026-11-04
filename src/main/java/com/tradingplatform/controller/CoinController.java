package com.tradingplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.tradingplatform.modal.Coin;
import com.tradingplatform.service.CoinService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/coins")
public class CoinController {
    @Autowired
    private CoinService coinService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping
    ResponseEntity<List<Coin>> getCoinList(@RequestParam(required = false,name="page") int page) {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}/chart")
    ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId, @RequestParam("days") int days) {
        String res = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(res);
        
        return new ResponseEntity<>(jsonNode, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String coin=coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    }
    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
        String coins=coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
     }

     @GetMapping("/trending")
     ResponseEntity<JsonNode> getTrendingCoin() throws Exception {
        String coins=coinService.getTrendingCoins();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
     }

    //  @GetMapping("/details/{coinId}")
    //  ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
    //     String coin=coinService.getCoinDetails(coinId);
    //     JsonNode jsonNode = objectMapper.readTree(coin);
    //     return ResponseEntity.ok(jsonNode);
    //  }

    @GetMapping("/details/{coinId}")
public ResponseEntity<?> getCoinDetails(@PathVariable String coinId) {
    try {
        String coin = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(coin);
        return ResponseEntity.ok(jsonNode);
    } catch (HttpClientErrorException.TooManyRequests e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                             .body("CoinGecko API limit exceeded. Try again later.");
    } catch (HttpClientErrorException.NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Coin not found: " + coinId);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                             .body("CoinGecko API error: " + e.getMessage());
    }
}



    

}
