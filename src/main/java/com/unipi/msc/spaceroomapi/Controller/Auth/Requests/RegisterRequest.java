package com.unipi.msc.spaceroomapi.Controller.Auth.Requests;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NonNull
    private String email;
    @NonNull
    private String username;
    @NonNull
    private String password;
    private String role;
    private String gender;
    private String firstName;
    private String lastName;
    private Long birthday;
}
