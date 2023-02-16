package com.unipi.msc.spaceroomapi.Model.Image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unipi.msc.spaceroomapi.Model.House.House;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue
    private Long Id;
    private String name;
    private String type;
    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="longblob not null")
    private byte[] imageData;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "house_id")
    @JsonBackReference
    private House house;

}
