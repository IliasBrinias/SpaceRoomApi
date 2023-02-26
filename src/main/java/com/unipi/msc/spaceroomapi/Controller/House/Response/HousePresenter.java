package com.unipi.msc.spaceroomapi.Controller.House.Response;

import com.unipi.msc.spaceroomapi.Controller.Image.Response.ImagePresenter;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HousePresenter {
    private Long Id;
    private Long hostId;
    private String title;
    private String description;
    private String location;
    private Integer maxCapacity;
    private Double price;
    private List<ImagePresenter> images = new ArrayList<>();
    public static HousePresenter getHousePresenter(House h){
        List<ImagePresenter> images = new ArrayList<>();
        if (h.getImages() != null) {
            for (Image i : h.getImages()) {
                images.add(ImagePresenter.builder()
                        .id(i.getId())
                        .link("/image/" + i.getId())
                        .build());
            }
        }
        return HousePresenter.builder()
                .Id(h.getId())
                .hostId(h.getHost().getId())
                .location(h.getLocation())
                .description(h.getDescription())
                .maxCapacity(h.getMaxCapacity())
                .price(h.getPrice())
                .title(h.getTitle())
                .images(images)
                .build();
    }
}
