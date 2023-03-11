package com.unipi.msc.spaceroomapi.Controller.User;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Auth.AuthenticationService;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Request.UserRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.UserPresenter;
import com.unipi.msc.spaceroomapi.Controller.Responses.UserReservationPresenter;
import com.unipi.msc.spaceroomapi.Model.Image.ImageRepository;
import com.unipi.msc.spaceroomapi.Model.Image.ImageService;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.*;
import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ImageService imageService;
    private final ReservationService reservationService;
    private final ImageRepository imageRepository;
    private final UserDaoService userDaoService;
    @GetMapping
    public ResponseEntity<?> getUser(){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = null;
        UserDao userDao = userDaoService.getLastToken(u).orElse(null);
        if (userDao != null) token = userDao.getToken();
        return ResponseEntity.ok(authenticationService.getAuthenticationResponse(u,token));
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUser(){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(u instanceof Admin)) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ACCESS_DENIED));
        List<UserPresenter> userPresenters = new ArrayList<>();
        for (User user:userService.getAllUsers()) {
            userPresenters.add(UserPresenter.getUser(user));
        }
        return ResponseEntity.ok(userPresenters);
    }
    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestBody UserRequest request) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Gender gender;
        try {
            gender = Gender.valueOf(request.getGender().toUpperCase());
            u.setGender(gender);
        }catch (Exception ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.GENDER_NOT_VALID));
        }
        if (request.getEmail()!=null){
            if (u.getIsGoogleAccount()) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.EMAIL_CANNOT_CHANGE_BECAUSE_IS_AN_GOOGLE_ACCOUNT));
            if (userService.getUserByEmail(request.getEmail()).isPresent()){
                return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.EMAIL_EXISTS));
            }
            u.setEmail(request.getEmail());
        }
        if (request.getUsername()!=null){
            if (userService.getUserByUsername(request.getUsername()).isPresent()){
                return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USERNAME_EXISTS));
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
                return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ROLE_DOESNT_EXIST));
            }
            u.setRole(role);
        }
        String token = null;
        UserDao userDao = userDaoService.getLastToken(u).orElse(null);
        if (userDao != null) token = userDao.getToken();
        u = userRepository.save(u);
        return ResponseEntity.ok(authenticationService.getAuthenticationResponse(u,token));
    }
    @PatchMapping("/image")
    public ResponseEntity<?> updateUserImage(@RequestParam("image") MultipartFile newImg) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            u.setImage(imageService.uploadUserImage(newImg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ErrorResponse(false, ErrorMessages.PLEASE_TRY_AGAIN_LATER));
        }
        userRepository.save(u);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteUserImage() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (u.getImage()==null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.PROFILE_NOT_FOUND));
        u.setImage(null);
        userRepository.save(u);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/reservation")
    public ResponseEntity<?> getUserReservation() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reservation> reservationList = new ArrayList<>();
        if (u instanceof Client){
            reservationList = ((Client) u).getReservations();
        }else if(u instanceof Host){
            reservationList = reservationService.getHousesReservation(((Host) u).getHouses());
        }
        return ResponseEntity.ok(UserReservationPresenter.getReservationPresenter(reservationList));
    }
}
