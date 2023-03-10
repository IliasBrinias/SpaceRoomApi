package com.unipi.msc.spaceroomapi.Model.House;

import com.unipi.msc.spaceroomapi.Model.User.Host;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;

    public List<House> getHouses() {
        return houseRepository.findAll();
    }
    public Optional<House> getHouse(Long Id) {
        return houseRepository.findById(Id);
    }

    public List<House> getHousesFromHost(Host host) {
        return houseRepository.findAllByHost(host);
    }
}
