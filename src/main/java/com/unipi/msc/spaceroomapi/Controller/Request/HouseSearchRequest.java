package com.unipi.msc.spaceroomapi.Controller.Request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HouseSearchRequest {
    private DateRange date;
    private String location;
}
