package com.unipi.msc.spaceroomapi.Model.Reservation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Message.Message;
import com.unipi.msc.spaceroomapi.Model.User.Client;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Long creationDate;
    private int uuid;
    private boolean checkIn;
    private Long dateFrom;
    private Long dateTo;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "house_id")
    @JsonBackReference
    private House house;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

}
