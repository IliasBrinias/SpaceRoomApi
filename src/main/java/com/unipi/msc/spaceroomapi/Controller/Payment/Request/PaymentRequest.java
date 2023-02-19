package com.unipi.msc.spaceroomapi.Controller.Payment.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Double price;
    private DateRangeRequest date;
    private Long houseId;
}
