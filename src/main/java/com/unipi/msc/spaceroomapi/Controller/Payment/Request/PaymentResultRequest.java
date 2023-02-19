package com.unipi.msc.spaceroomapi.Controller.Payment.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultRequest {
    public Long reservationId;
    public String status;
}
