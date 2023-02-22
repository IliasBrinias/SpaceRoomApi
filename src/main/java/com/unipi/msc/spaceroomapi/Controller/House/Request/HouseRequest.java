package com.unipi.msc.spaceroomapi.Controller.House.Request;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HouseRequest {
    String title;
    String description;
    Integer maxCapacity;
    Double price;
    List<MultipartFile> images;
}
