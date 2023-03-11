package com.unipi.msc.spaceroomapi.Model.Reservation;

import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Optional<Reservation> getReservationWithId(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
    public List<Reservation> getHouseReservation(House h) {
        return reservationRepository.findAllByHouseAndStatusOrderByDateFromAsc(h, ReservationStatus.SUCCESS);
    }
    public boolean isAvailable(House h,Long from, Long to) {
        List<Reservation> reservations = reservationRepository.findAllByHouseAndDateFromIsGreaterThanEqualAndDateToIsLessThanEqual(h,from, to);
        return reservations.isEmpty();
    }

    public List<Reservation> getHousesReservation(List<House> houses) {
        return reservationRepository.findAllByHouseInOrderByDateFromAsc(houses);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllByOrderByDateFromAsc();
    }
}
