package com.unipi.msc.spaceroomapi.Controller.User;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Auth.AuthenticationService;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Request.UserRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ReservationPresenter;
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
        return userService.updateUserDetails(request, u);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Admin)) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NOT_AUTHORIZED));
        }
        User u = userService.getUser(id).orElse(null);
        if (u == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USER_NOT_FOUND));
        userRepository.delete(u);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserById(@RequestBody UserRequest request, @PathVariable Long id) {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Admin)) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NOT_AUTHORIZED));
        }
        User u = userService.getUser(id).orElse(null);
        if (u == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USER_NOT_FOUND));
        return userService.updateUserDetails(request, u);
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
        List<ReservationPresenter> presenters = new ArrayList<>();
        for (Reservation reservation:reservationList){
            presenters.add(ReservationPresenter.getReservation(reservation));
        }
        return ResponseEntity.ok(presenters);
    }
}
