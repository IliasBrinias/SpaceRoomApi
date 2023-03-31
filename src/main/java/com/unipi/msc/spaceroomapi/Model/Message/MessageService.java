package com.unipi.msc.spaceroomapi.Model.Message;

import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.User.Host;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;


    public List<Message> getMessagesForReservation(Reservation reservation) {
        return messageRepository.findAllByReservationOrderByDateAsc(reservation);
    }
}
