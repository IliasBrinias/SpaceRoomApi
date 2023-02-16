package com.unipi.msc.spaceroomapi.Model.House;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;
}
