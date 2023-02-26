package com.unipi.msc.spaceroomapi.Controller.Auth;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.LoginRequest;
import com.unipi.msc.spaceroomapi.Controller.Request.RegisterRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ImagePresenter;
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
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ROLE_DOESNT_EXIST));
        }
        // build user object and save it
        User user;
        if (role == Role.CLIENT){
            user = new Client(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    Gender.OTHER,
                    null,
                    null,
                    null
            );
        }
        else if (role==Role.HOST){
            user = new Host(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    Gender.OTHER,
                    null,
                    null,
                    null
            );
        }
        else if (role==Role.ADMIN){
            user = new Admin(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    role,
                    Gender.OTHER,
                    null,
                    null,
                    null
            );
        }else {
            user = new User(request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    Role.USER,
                    Gender.OTHER,
                    null,
                    null,
                    null
            );
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
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) user = userRepository.findByEmail(request.getUsername()).orElse(null);
        if (user == null) return  ResponseEntity.notFound().build();
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
        if (user instanceof Host){
            Host d = (Host) user;
        }
        if (user.getImage()!=null){
            response.setImage(ImagePresenter.builder()
                            .link("/image/"+user.getImage().getId())
                            .id(user.getImage().getId())
                    .build());
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

    public ResponseEntity<?> loginWithGoogle(LoginRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return authenticate(LoginRequest.builder()
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .build());
        }
        return register(RegisterRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build());
    }
}
