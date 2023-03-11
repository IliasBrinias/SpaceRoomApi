package com.unipi.msc.spaceroomapi.Controller.Responses;

import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.User.User;
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
    public static UserPresenter getUser(User u){
        ImagePresenter imagePresenter = null;
        if (u.getImage()!=null){
            imagePresenter = ImagePresenter.builder()
                    .id(u.getImage().getId())
                    .link("/image/" + u.getId())
                    .build();
        }
        return UserPresenter.builder()
                .id(u.getId())
                .email(u.getEmail())
                .username(u.getUsername())
                .image(imagePresenter)
                .role(u.getRole())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .gender(u.getGender())
                .birthday(u.getBirthday())
                .creationDate(u.getCreationDate())
                .build();
    }
}
