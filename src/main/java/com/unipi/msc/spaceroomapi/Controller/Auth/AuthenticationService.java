package com.unipi.msc.spaceroomapi.Controller.Auth;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Auth.Requests.LoginRequest;
import com.unipi.msc.spaceroomapi.Controller.Auth.Requests.RegisterRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.UserPresenter;
import com.unipi.msc.spaceroomapi.Model.User.*;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoRepository;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoService;
import com.unipi.msc.spaceroomapi.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDaoService userDaoService;
    private final UserDaoRepository userDaoRepository;

    public ResponseEntity<?> register(RegisterRequest request) {
        // check for empty data
        if (request.getUsername().equals("") || request.getEmail().equals("") || request.getPassword().equals("")) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.FILL_ALL_THE_FIELDS));
        }

        // check if the user exists
        String error_msg = checkIfExist(request);
        if (!error_msg.equals("")) return ResponseEntity.badRequest().body(new ErrorResponse(false,error_msg));


        Role role;
        Gender gender;
        try {
            role = Role.valueOf(request.getRole());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ROLE_DOESNT_EXIST));
        }
        try {
            gender = Gender.valueOf(request.getGender());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ROLE_DOESNT_EXIST));
        }

        // build user object and save it
        User user;
        if (role == Role.CLIENT){
            user = new Client(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    gender,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getBirthday());
        }
        else if (role==Role.OWNER){
            user = new Owner(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    gender,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getBirthday()
            );
        }
        else if (role==Role.ADMIN){
            user = new Admin(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    gender,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getBirthday());
        }else {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ROLE_IS_NULL));
        }
        user.setCreationDate(new Date().getTime());
        user = userRepository.save(user);

        String generatedToken = jwtService.generateToken(user);
        userDaoRepository.save(UserDao.builder()
                .token(generatedToken)
                .created(new Date().getTime())
                .user(user)
                .isActive(true)
                .build());

        return ResponseEntity.ok(getAuthenticationResponse(user, generatedToken));
    }
    public ResponseEntity<?> authenticate(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        // double check if the user exists
        if (userRepository.findByUsername(request.getUsername()).isEmpty()) return ResponseEntity.notFound().build();
        User user = userRepository.findByUsername(request.getUsername()).get();
        return ResponseEntity.ok(getAuthenticationResponse(user, jwtService.generateToken(user)));
    }
    public UserPresenter getAuthenticationResponse(User user, String jwtToken) {
        UserPresenter response = UserPresenter.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .creationDate(user.getCreationDate())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .token(jwtToken).build();
        if (user instanceof Client){
            Client c = (Client) user;
        }
        if (user instanceof Owner){
            Owner d = (Owner) user;
        }
        return response;
    }
    public String checkIfExist(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ErrorMessages.USERNAME_EXISTS;
        }else if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return ErrorMessages.EMAIL_EXISTS;
        }
        return "";
    }
    public ResponseEntity<?> logout() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDao userDao = userDaoService.getLastToken(u).orElse(null);
        if (userDao == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NO_TOKEN_FOUND));
        userDao.setIsActive(false);
        userDaoRepository.save(userDao);
        return ResponseEntity.ok().build();
    }
}
