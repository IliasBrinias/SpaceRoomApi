package com.unipi.msc.spaceroomapi.Controller.Request;

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
    String location;
    Integer maxCapacity;
    Double price;
    List<MultipartFile> images;
}
