package com.unipi.msc.spaceroomapi.Model.User;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.House.House;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue(value = "OWNER")
public class Owner extends User{
    @Id
    @GeneratedValue
    private Long Id;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<House> houses = new ArrayList<>();
    public Owner(@NonNull String email, @NonNull String username, String password, @NonNull Role role, Gender gender, String firstName, String lastName, Long birthday) {
        super(email, username, password, role, gender, firstName, lastName, birthday);
    }
}
