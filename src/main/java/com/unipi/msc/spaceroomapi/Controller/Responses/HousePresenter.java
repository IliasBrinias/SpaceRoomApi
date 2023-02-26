package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.unipi.msc.spaceroomapi.Controller.Request.DateRange;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
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
    private List<DateRange> date = new ArrayList<>();
    private List<ImagePresenter> images = new ArrayList<>();
    public static HousePresenter getHouse(House h){
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
    public static HousePresenter getHouseWithReservationDates(House h, List<Reservation> reservations){
        HousePresenter housePresenter = getHouse(h);
        housePresenter.setDate(new ArrayList<>());
        reservations.forEach(reservation -> housePresenter.getDate().add(DateRange.builder()
                        .from(reservation.getDateFrom())
                        .to(reservation.getDateTo())
                .build()));
        return housePresenter;
    }
}
