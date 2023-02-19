package com.unipi.msc.spaceroomapi.Controller.Payment.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeRequest {
    private Long from;
    private Long to;
}
