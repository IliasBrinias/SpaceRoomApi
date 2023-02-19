package com.unipi.msc.spaceroomapi.Model.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Optional<Reservation> findById(Long Id);
}