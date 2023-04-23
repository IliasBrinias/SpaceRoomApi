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
    private Long hostId;
    private Long creationDate;
    private int uuid;
    private boolean checkIn;
    private String username;
    private String houseTitle;
    private QRPresenter qr;

    public static ReservationPresenter getReservation(Reservation r){
        ReservationPresenter presenter = ReservationPresenter.builder()
                .Id(r.getId())
                .price(r.getPrice())
                .date(DateRange.builder()
                        .from(r.getDateFrom())
                        .to(r.getDateTo())
                        .build())
                .creationDate(r.getCreationDate())
                .uuid(r.getUuid())
                .status(r.getStatus())
                .checkIn(r.isCheckIn())
                .clientId(r.getClient().getId())
                .houseId(r.getHouse().getId())
                .username(r.getClient().getUsername())
                .houseTitle(r.getHouse().getTitle())
                .qr(QRPresenter.builder()
                        .link("reservation/"+r.getId()+"/qr")
                        .build())
                .hostId(r.getHouse().getHost().getId())
                .build();
        return presenter;
    }
}
