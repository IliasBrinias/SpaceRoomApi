package com.unipi.msc.spaceroomapi.Model.Reservation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.User.Client;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue
    private Long Id;
    private Double price;
    private ReservationStatus status;
    private Long dateFrom;
    private Long dateTo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "house_id")
    @JsonBackReference
    private House house;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
