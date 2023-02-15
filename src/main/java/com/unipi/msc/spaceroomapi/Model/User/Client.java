package com.unipi.msc.spaceroomapi.Model.User;

import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue(value = "CLIENT")
public class Client extends User{
    @Id
    @GeneratedValue
    private Long Id;

    public Client(@NonNull String email, @NonNull String username, String password, @NonNull Role role, Gender gender, String firstName, String lastName, Long birthday) {
        super(email, username, password, role, gender, firstName, lastName, birthday);
    }
}
