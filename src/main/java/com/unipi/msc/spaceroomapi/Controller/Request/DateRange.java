package com.unipi.msc.spaceroomapi.Controller.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {
    private Long from;
    private Long to;
}
