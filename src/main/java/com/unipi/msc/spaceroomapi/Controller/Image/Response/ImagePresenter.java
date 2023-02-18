package com.unipi.msc.spaceroomapi.Controller.Image.Response;

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
