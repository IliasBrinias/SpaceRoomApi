package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.unipi.msc.spaceroomapi.Model.Message.Message;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.User.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessagePresenter {
    private Long Id;
    private String msg;
    private Long date;
    private Long senderId;
    private Long reservationId;

    public static MessagePresenter getPresenter(Message message){
        return MessagePresenter.builder()
                .Id(message.getId())
                .msg(message.getMsg())
                .date(message.getDate())
                .senderId(message.getSender().getId())
                .reservationId(message.getReservation().getId())
                .build();
    }
}
