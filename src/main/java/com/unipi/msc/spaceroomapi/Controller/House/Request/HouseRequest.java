package com.unipi.msc.spaceroomapi.Controller.House.Request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HouseRequest {
    private String title;
    private String description;
    private Integer maxCapacity;
    private Double price;
    private List<MultipartFile> images;
}
