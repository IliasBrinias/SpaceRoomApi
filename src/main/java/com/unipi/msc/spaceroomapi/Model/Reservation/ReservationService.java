package com.unipi.msc.spaceroomapi.Model.Reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Optional<Reservation> getReservationWithId(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
