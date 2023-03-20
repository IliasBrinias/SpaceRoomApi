package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.unipi.msc.spaceroomapi.Controller.Request.DateRange;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReservationPresenter {
    private Long Id;
    private Double price;
    private String status;
    private DateRange date;
    private HousePresenter house;
    public static List<UserReservationPresenter> getReservationPresenter(List<Reservation> reservations){
        List<UserReservationPresenter> reservationPresenters = new ArrayList<>();
        for (Reservation reservation:reservations){
            reservationPresenters.add(UserReservationPresenter.builder()
                    .Id(reservation.getId())
                    .price(reservation.getPrice())
                    .status(reservation.getStatus().toString())
                    .status(reservation.getStatus().toString())
                    .date(DateRange.builder()
                            .from(reservation.getDateFrom())
                            .to(reservation.getDateTo())
                            .build())
                    .house(HousePresenter.getHouse(reservation.getHouse()))
                    .build());
        }
        return reservationPresenters;
    }
}
