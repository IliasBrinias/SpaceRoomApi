package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPresenter {
    private Long id;
    private String email;
    private String username;
    private ImagePresenter image;
    private Role role;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Long birthday;
    private String token;
    private Long creationDate;
}
