package com.unipi.msc.spaceroomapi.Model.House;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.User.Host;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class House {
    @Id
    @GeneratedValue
    private Long Id;
    private String title;
    private String location;
    private String description;
    private Integer maxCapacity;
    private Double price;
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "host_id")
    @JsonBackReference
    private Host host;

}
