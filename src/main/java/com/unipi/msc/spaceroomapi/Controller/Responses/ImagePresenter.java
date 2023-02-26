package com.unipi.msc.spaceroomapi.Controller.Responses;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImagePresenter {
    private Long id;
    private String link;
}
