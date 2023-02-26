package com.unipi.msc.spaceroomapi.Controller.House;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.HouseRequest;
import com.unipi.msc.spaceroomapi.Controller.Request.HouseSearchRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.HousePresenter;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.ReservationPresenter;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.House.HouseRepository;
import com.unipi.msc.spaceroomapi.Model.House.HouseService;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.Image.ImageRepository;
import com.unipi.msc.spaceroomapi.Model.Image.ImageService;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.Host;
import com.unipi.msc.spaceroomapi.Shared.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;
    private final HouseRepository houseRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final ReservationService reservationService;
    @PostMapping("/search")
    public ResponseEntity<?> getAllHouses(@RequestBody HouseSearchRequest request){
        List<HousePresenter> housePresenters = new ArrayList<>();
        houseService.getHouses().stream()
                .filter(h->{
                    if (request.getDate()!=null){
                        return reservationService.isAvailable(h,request.getDate().getFrom(),request.getDate().getTo());
                    }
                    return true;
                })
                .filter(h->{
                    if (request.getLocation()!=null){
                        return h.getLocation().contains(request.getLocation());
                    }
                    return true;
                })
                .forEach(h->housePresenters.add(HousePresenter.getHouse(h)));
        return ResponseEntity.ok(housePresenters);
    }
    @GetMapping
    public ResponseEntity<?> getHouses(){
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        List<HousePresenter> housePresenters = new ArrayList<>();
        for (House h:houseService.getHousesFromHost(host)){
            housePresenters.add(HousePresenter.getHouse(h));
        }
        return ResponseEntity.ok(housePresenters);
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getHouse(@PathVariable Long id){
        House h = houseService.getHouse(id).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        List<Reservation> reservations = reservationService.getHouseReservation(h);
        return ResponseEntity.ok(HousePresenter.getHouseWithReservationDates(h,reservations));
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addHouse(@ModelAttribute HouseRequest request) {
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }

        if (request.getTitle() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.TITLE_IS_OBLIGATORY));
        if (request.getLocation() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.LOCATION_IS_OBLIGATORY));
        if (request.getMaxCapacity() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.MAX_CAPACITY_IS_OBLIGATORY));
        if (request.getMaxCapacity() <= 0) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.MAX_CAPACITY_MUST_BE_GREATER_THAN_ZERO));
        if (request.getPrice() == null)return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.PRICE_IS_OBLIGATORY));
        if (request.getPrice() <= 0) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.PRICE_MUST_BE_GREATER_THAN_ZERO));

        House h = houseRepository.save(
                House.builder()
                .title(request.getTitle())
                .location(request.getLocation())
                .description(request.getDescription())
                .maxCapacity(request.getMaxCapacity())
                .price(request.getPrice())
                .host(host)
                .build());
        h.setImages(new ArrayList<>());
        if (request.getImages()!=null){
            try {
                for (MultipartFile image : request.getImages()) {
                    if (image.isEmpty()) continue;
                    h.getImages().add(imageService.uploadHouseImage(image,h));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body(new ErrorResponse(false, ErrorMessages.PLEASE_TRY_AGAIN_LATER));
            }
        }
        return ResponseEntity.ok(HousePresenter.getHouse(h));
    }
    @PatchMapping("{id}/edit")
    public ResponseEntity<?> updateHouses(@PathVariable Long id, @RequestBody HouseRequest request) {
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        if (request.getTitle() == null &&
            request.getDescription() == null &&
            request.getMaxCapacity() == null &&
            request.getPrice() == null &&
            request.getImages() == null){

            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.BODY_CANNOT_BE_EMPTY));
        }
        House h = houseService.getHouse(id).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        if (!h.getHost().getId().equals(host.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ONLY_THE_HOST_CAN_EDIT_THE_HOUSE));
        if (request.getTitle() != null){
            if (request.getTitle().equals("")) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.TITLE_CANT_BE_EMPTY));
            }
            h.setTitle(request.getTitle());
        }
        if (request.getLocation() != null){
            if (request.getLocation().equals("")) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.LOCATION_CANT_BE_EMPTY));
            }
            h.setLocation(request.getLocation());
        }
        if (request.getDescription() != null){
            if (request.getDescription().equals("")) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.DESCRIPTION_CANNOT_BE_EMPTY));
            }
            h.setDescription(request.getDescription());
        }
        if (request.getMaxCapacity() != null) {
            if (request.getMaxCapacity() > 0) h.setMaxCapacity(request.getMaxCapacity());
        }
        if (request.getPrice() != null) {
            if (request.getPrice() > 0) h.setPrice(request.getPrice());
        }
        return ResponseEntity.ok(HousePresenter.getHouse(h));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable Long id){
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        House h = houseService.getHouse(id).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        if (!h.getHost().getId().equals(host.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ONLY_THE_HOST_CAN_EDIT_THE_HOUSE));
        houseRepository.delete(h);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{houseId}/image/{id}")
    public ResponseEntity<?> deleteHouseImage(@PathVariable Long houseId, @PathVariable Long id){
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        House h = houseService.getHouse(houseId).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        if (!h.getHost().getId().equals(host.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ONLY_THE_HOST_CAN_EDIT_THE_HOUSE));
        Image image = h.getImages().stream()
                .filter(img -> Objects.equals(img.getId(), id))
                .findFirst().orElse(null);
        if (image == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.IMAGE_NOT_FOUND));
        imageRepository.delete(image);
        h.getImages().remove(image);
        houseRepository.save(h);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("{houseId}/image/{id}")
    public ResponseEntity<?> editHouseImage(@PathVariable Long houseId, @PathVariable Long id, @RequestParam("image") MultipartFile newImg){
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        House h = houseService.getHouse(houseId).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        if (!h.getHost().getId().equals(host.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ONLY_THE_HOST_CAN_EDIT_THE_HOUSE));
        Image image = h.getImages().stream()
                .filter(img -> Objects.equals(img.getId(), id))
                .findFirst().orElse(null);
        if (image == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.IMAGE_NOT_FOUND));
        try {
            image.setImageData(ImageUtils.compressImage(newImg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ErrorResponse(false, ErrorMessages.PLEASE_TRY_AGAIN_LATER));
        }
        imageRepository.save(image);
        return ResponseEntity.ok().build();
    }
    @PostMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editHouseImage(@PathVariable Long id, @RequestParam("image") MultipartFile newImg) {
        Host host;
        try {
            host = (Host) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException ignore){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.USER_MUST_BE_HOST));
        }
        House h = houseService.getHouse(id).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        if (!h.getHost().getId().equals(host.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.ONLY_THE_HOST_CAN_EDIT_THE_HOUSE));
        try {
            imageService.uploadHouseImage(newImg,h);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ErrorResponse(false, ErrorMessages.PLEASE_TRY_AGAIN_LATER));
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("{id}/reservation")
    public ResponseEntity<?> getHouseReservation(@PathVariable Long id) {
        House h = houseService.getHouse(id).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.HOUSE_NOT_FOUND));
        List<ReservationPresenter> response = new ArrayList<>();
        reservationService.getHouseReservation(h).forEach(r-> response.add(ReservationPresenter.getReservation(r)));
        return ResponseEntity.ok(response);
    }
}
