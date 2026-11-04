package com.tradingplatform.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double quantity;

    @ManyToOne
    private Coin coin;

    private double buyPrice;

    private double sellPrice;

    // json ignore to avoid circular reference during serialization 
    // in simple words it prevents the order from being included in the JSON representation of the order item, which can help avoid issues like infinite recursion when serializing objects that reference each other.  
    @JsonIgnore 
    @OneToOne
    private Order order;
}
