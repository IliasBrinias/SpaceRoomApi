package com.unipi.msc.spaceroomapi.Controller.User.Request;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String gender;
    private String firstName;
    private String lastName;
    private Long birthday;

}
