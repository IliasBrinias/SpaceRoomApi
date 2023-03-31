package com.unipi.msc.spaceroomapi.Controller.Request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String msg;
    private Long date;
}
