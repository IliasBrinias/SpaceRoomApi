package com.unipi.msc.spaceroomapi.Controller.Auth;

import com.unipi.msc.spaceroomapi.Controller.Auth.Requests.LoginRequest;
import com.unipi.msc.spaceroomapi.Controller.Auth.Requests.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return authenticationService.register(request);
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request){
        return authenticationService.authenticate(request);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        return authenticationService.logout();
    }

}
