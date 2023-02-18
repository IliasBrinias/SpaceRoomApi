package com.unipi.msc.spaceroomapi.Controller.User;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Auth.AuthenticationService;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.User.Request.UserRequest;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.Image.ImageRepository;
import com.unipi.msc.spaceroomapi.Model.Image.ImageService;
import com.unipi.msc.spaceroomapi.Model.User.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.User.User;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDaoService;
import com.unipi.msc.spaceroomapi.Model.User.UserRepository;
import com.unipi.msc.spaceroomapi.Model.User.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ImageService imageService;
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
        if (request.getFirstName()!=null) u.setFirstName(request.getFirstName());
        if (request.getLastName()!=null) u.setLastName(request.getLastName());
        if (request.getBirthday()!=null) u.setBirthday(request.getBirthday());
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
}
