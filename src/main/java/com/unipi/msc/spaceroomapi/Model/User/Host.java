package com.unipi.msc.spaceroomapi.Model.User;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
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
@DiscriminatorValue(value = "HOST")
public class Host extends User{
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<House> houses = new ArrayList<>();
    public Host(String email, String username, String password, @NonNull Role role, Gender gender, String firstName, String lastName, Long birthday) {
        super(email, username, password, role, gender, firstName, lastName, birthday);
    }
}
