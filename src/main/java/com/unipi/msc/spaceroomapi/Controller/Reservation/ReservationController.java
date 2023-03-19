package com.unipi.msc.spaceroomapi.Controller.Reservation;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.ReservationRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.ReservationPresenter;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.House.HouseService;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationRepository;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.Admin;
import com.unipi.msc.spaceroomapi.Model.User.Client;
import com.unipi.msc.spaceroomapi.Model.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final HouseService houseService;
    @GetMapping("reservation/all")
    public ResponseEntity<?> getAllReservations() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(u instanceof Admin)) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.ACCESS_DENIED));
        List<ReservationPresenter> reservationPresenterList = new ArrayList<>();
        for (Reservation reservation:reservationService.getAllReservations()){
            reservationPresenterList.add(ReservationPresenter.getReservation(reservation));
        }
        return ResponseEntity.ok(reservationPresenterList);
    }
    @PostMapping("/house/{houseId}/reservation")
    public ResponseEntity<?> reservation(@RequestBody ReservationRequest request, @PathVariable Long houseId) {
        Client client;
        try {
            client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USER_MUST_BE_CLIENT));
        }
        if (houseId == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.HOUSE_ID_IS_OBLIGATORY));
        if (request.getPrice() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.PRICE_IS_OBLIGATORY));
        if (request.getDate() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.DATE_IS_OBLIGATORY));
        House h = houseService.getHouse(houseId).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.HOUSE_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .client(client)
                .house(h)
                .price(request.getPrice())
                .dateFrom(request.getDate().getFrom())
                .dateTo(request.getDate().getTo())
                .creationDate(new Date().getTime())
                .uuid(UUID.randomUUID().hashCode())
                .status(ReservationStatus.SUCCESS)
                .build();

        reservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationPresenter.getReservation(reservation));
    }
    @PostMapping("/reservation/{id}/reject")
    public ResponseEntity<?> rejectReservation(@PathVariable Long id) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Reservation reservation = reservationService.getReservationWithId(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_NOT_FOUND));
        }
        if (Objects.equals(reservation.getClient().getId(), u.getId()) || Objects.equals(reservation.getHouse().getHost().getId(), u.getId())){
            reservation.setStatus(ReservationStatus.REJECTED);
        }else {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NOT_AUTHORIZED_TO_REJECT_THIS_RESERVATION));
        }
        reservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationPresenter.getReservation(reservation));
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationWithId(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_NOT_FOUND));
        }
        return ResponseEntity.ok(ReservationPresenter.getReservation(reservation));
    }
    @DeleteMapping("reservation/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(u instanceof Admin)) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.NOT_AUTHORIZED));
        Reservation reservation = reservationService.getReservationWithId(id).orElse(null);
        if (reservation == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.RESERVATION_NOT_FOUND));
        reservationRepository.delete(reservation);
        return ResponseEntity.ok().build();
    }
}
