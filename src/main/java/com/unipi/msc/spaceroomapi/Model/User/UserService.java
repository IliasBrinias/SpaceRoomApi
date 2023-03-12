package com.unipi.msc.spaceroomapi.Model.User;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Auth.AuthenticationService;
import com.unipi.msc.spaceroomapi.Controller.Request.UserRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final UserDaoService userDaoService;
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public List<User> getUsers() {
        return userRepository.findAllByRoleNot(Role.ADMIN);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findAllByIdIs(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public ResponseEntity<?> updateUserDetails(UserRequest request, User u) {
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
            u.setGender(gender);
        }catch (Exception ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.GENDER_NOT_VALID));
        }
        if (request.getEmail()!=null){
            if (u.getIsGoogleAccount())
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.EMAIL_CANNOT_CHANGE_BECAUSE_IS_AN_GOOGLE_ACCOUNT));
            if (getUserByEmail(request.getEmail()).isPresent()){
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.EMAIL_EXISTS));
            }
            u.setEmail(request.getEmail());
        }
        if (request.getUsername()!=null){
            if (getUserByUsername(request.getUsername()).isPresent()){
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USERNAME_EXISTS));
            }
            u.setUsername(request.getUsername());
        }
        if (request.getFirstName()!=null) u.setFirstName(request.getFirstName());
        if (request.getLastName()!=null) u.setLastName(request.getLastName());
        if (request.getBirthday()!=null) u.setBirthday(request.getBirthday());
        if (u.getRole() == Role.USER && request.getRole()!=null){
            Role role;
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            }catch (Exception ignore){
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ROLE_DOESNT_EXIST));
            }
            u.setRole(role);
        }
        String token = null;
        UserDao userDao = userDaoService.getLastToken(u).orElse(null);
        if (userDao != null) token = userDao.getToken();
        u = userRepository.save(u);
        return ResponseEntity.ok(authenticationService.getAuthenticationResponse(u, token));
    }

}
