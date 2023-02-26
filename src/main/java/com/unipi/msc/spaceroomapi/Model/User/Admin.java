package com.unipi.msc.spaceroomapi.Model.User;

import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue(value = "ADMIN")
public class Admin extends User{
    @Id
    @GeneratedValue
    private Long Id;
    public Admin(String email, String username, String password, @NonNull Role role, Gender gender, String firstName, String lastName, Long birthday) {
        super(email, username, password, role, gender, firstName, lastName, birthday);
    }

}
