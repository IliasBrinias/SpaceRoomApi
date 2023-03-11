package com.unipi.msc.spaceroomapi.Model.Reservation;

import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Optional<Reservation> findById(Long Id);
    List<Reservation> findAllByHouseAndStatusOrderByDateFromAsc(House house, ReservationStatus status);
    List<Reservation> findAllByHouseAndDateFromIsGreaterThanEqualAndDateToIsLessThanEqual(House house, Long dateFrom, Long dateTo);
    List<Reservation> findAllByHouseInOrderByDateFromAsc(Collection<House> house);
    List<Reservation> findAllByOrderByDateFromAsc();
}