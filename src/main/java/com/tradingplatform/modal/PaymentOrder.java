package com.tradingplatform.modal;

import com.tradingplatform.domain.PaymentMethod;
import com.tradingplatform.domain.PaymentOrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Long amount;

    private PaymentOrderStatus PaymentOrderStatus;

    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;

}
