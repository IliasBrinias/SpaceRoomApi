package com.unipi.msc.spaceroomapi.Controller.Request;

import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String email;
    private String password;
    private String role;
}
