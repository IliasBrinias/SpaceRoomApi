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
        if (request.getUsername() == null &&
                request.getGender() == null &&
                request.getBirthday() == null &&
                request.getEmail() == null &&
                request.getFirstName() == null &&
                request.getLastName() == null){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.BODY_CANNOT_BE_EMPTY));
        }
        if (u.getGender()!=null){
            Gender gender;
            try {
                gender = Gender.valueOf(request.getGender().toUpperCase());
                u.setGender(gender);
            }catch (Exception ignore){
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.GENDER_NOT_VALID));
            }
        }
        if (request.getRole() != null) {
            Role role;
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
                if (role != Role.USER && u.getRole()==Role.USER) {
                    u.setRole(role);
                }
            } catch (Exception ignore) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ROLE_DOESNT_EXIST));
            }
        }
        if (request.getEmail()!=null){
            if (!request.getEmail().equals(u.getEmail())){
                if (u.getIsGoogleAccount())
                    return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.EMAIL_CANNOT_CHANGE_BECAUSE_IS_AN_GOOGLE_ACCOUNT));
                if (getUserByEmail(request.getEmail()).isPresent()){
                    return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.EMAIL_EXISTS));
                }
                u.setEmail(request.getEmail());
            }
        }
        if (request.getUsername()!=null){
            if (!request.getUsername().equals(u.getUsername())){
                if (getUserByUsername(request.getUsername()).isPresent()){
                    return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USERNAME_EXISTS));
                }
                u.setUsername(request.getUsername());
            }
        }
        if (request.getFirstName()!=null) {
            if (!request.getFirstName().equals(u.getFirstName())) {
                u.setFirstName(request.getFirstName());
            }
        }
        if (request.getLastName()!=null) {
            if (!request.getLastName().equals(u.getLastName())){
                u.setLastName(request.getLastName());
            }
        }
        if (request.getBirthday()!=null) {
            if (!request.getBirthday().equals(u.getBirthday())) {
                u.setBirthday(request.getBirthday());
            }
        }
        u = userRepository.save(u);
        String token = null;
        UserDao userDao = userDaoService.getLastToken(u).orElse(null);
        if (userDao!=null){
            token = userDao.getToken();
        }
        return ResponseEntity.ok(authenticationService.getAuthenticationResponse(u, token));
    }
}
