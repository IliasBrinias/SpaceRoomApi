package com.unipi.msc.spaceroomapi.Model.Reservation;

import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.User.Host;
import com.unipi.msc.spaceroomapi.Model.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public Optional<Reservation> getReservationWithId(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
    public List<Reservation> getHouseReservationWithSuccessStatus(House h) {
        return reservationRepository.findAllByHouseAndStatusOrderByDateFromAsc(h, ReservationStatus.SUCCESS);

    }
    public List<Reservation> getHouseReservations(House h) {
        return reservationRepository.findAllByHouse(h);
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

    public List<Reservation> getHostReservations(Host h) {
        return reservationRepository.findAllByHouseIn(h.getHouses());
    }
}
