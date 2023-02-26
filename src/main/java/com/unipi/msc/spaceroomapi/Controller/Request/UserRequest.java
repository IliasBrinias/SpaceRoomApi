package com.unipi.msc.spaceroomapi.Controller.Request;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String email;
    private String username;
    private String gender;
    private String firstName;
    private String lastName;
    private Long birthday;
    private String role;

}
