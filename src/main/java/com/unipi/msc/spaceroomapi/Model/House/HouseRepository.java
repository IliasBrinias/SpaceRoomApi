package com.unipi.msc.spaceroomapi.Model.House;

import com.unipi.msc.spaceroomapi.Model.User.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House,Long> {
    List<House> findAll();
    Optional<House> findById(Long Id);
    List<House> findAllByHost(Host host);
}