package com.unipi.msc.spaceroomapi.Controller.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    private Double price;
    private DateRange date;
    private Long houseId;
}
