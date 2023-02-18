package com.unipi.msc.spaceroomapi.Model.House;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House,Long> {
    List<House> findAll();
    Optional<House> findById(Long Id);
}