package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.unipi.msc.spaceroomapi.Controller.Request.DateRange;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPresenter {
    private Long Id;
    private Double price;
    private ReservationStatus status;
    private DateRange date;
    private Long clientId;
    private Long houseId;
    private Long creationDate;
    private int uuid;
    public static ReservationPresenter getReservation(Reservation r){
        return ReservationPresenter.builder()
                .Id(r.getId())
                .price(r.getPrice())
                .date(DateRange.builder()
                        .from(r.getDateFrom())
                        .to(r.getDateTo())
                        .build())
                .creationDate(r.getCreationDate())
                .uuid(r.getUuid())
                .status(r.getStatus())
                .clientId(r.getClient().getId())
                .houseId(r.getHouse().getId())
                .build();
    }
}
