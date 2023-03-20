package com.unipi.msc.spaceroomapi.Controller.Auth;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.LoginRequest;
import com.unipi.msc.spaceroomapi.Controller.Request.RegisterRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ImagePresenter;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.UserPresenter;
import com.unipi.msc.spaceroomapi.Model.User.*;
import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoRepository;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoService;
import com.unipi.msc.spaceroomapi.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public ResponseEntity<?> register(RegisterRequest request,Boolean isGoogleAccount) {
        // check for empty data
        if ((request.getUsername()==null && request.getEmail()==null) || request.getPassword()==null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.FILL_ALL_THE_FIELDS));
        }
        // check if the user exists
        String error_msg = checkIfExist(request);
        if (error_msg!=null) return ResponseEntity.badRequest().body(new ErrorResponse(false,error_msg));

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
        user.setIsGoogleAccount(isGoogleAccount);
        user.setCreationDate(new Date().getTime());
        user = userRepository.save(user);

        String generatedToken = generateToken(user);

        return ResponseEntity.ok(getAuthenticationResponse(user, generatedToken));
    }
    public ResponseEntity<?> authenticate(LoginRequest request) {
        User user;
        if (request.getUsername()!=null){
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
            user = userRepository.findByUsername(request.getUsername()).orElse(null);
        }else {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
            user = userRepository.findByEmail(request.getEmail()).orElse(null);
        }
        if (user == null) return  ResponseEntity.notFound().build();
        String token = generateToken(user);
        return ResponseEntity.ok(getAuthenticationResponse(user, token));
    }

    public String generateToken(User user) {
        String generatedToken = jwtService.generateToken(new User(){
            @Override
            public String getUsername() {
                if (user.getUsername()!=null){
                    return user.getUsername();
                }else {
                    return user.getEmail();
                }
            }
        });
        userDaoService.disableOldUsersToken(user);
        userDaoRepository.save(UserDao.builder()
                .token(generatedToken)
                .created(new Date().getTime())
                .user(user)
                .isActive(true)
                .build());
        return generatedToken;
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
        if (user.getImage()!=null){
            response.setImage(ImagePresenter.builder()
                    .link("/image/"+user.getImage().getId())
                    .id(user.getImage().getId())
                .build());
        }
        return response;
    }
    public String checkIfExist(RegisterRequest request) {
        if (request.getUsername()==null && request.getEmail()==null) return ErrorMessages.EMAIL_AND_USERNAME_ARE_NULL;
        if (request.getUsername()!=null){
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ErrorMessages.USERNAME_EXISTS;
            }
        }
        if (request.getEmail() != null){
            if (userRepository.findByEmail(request.getEmail()).isPresent()){
                return ErrorMessages.EMAIL_EXISTS;
            }
        }
        return null;
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
                .role(Role.USER.toString())
                .build(), true);
    }
}
