package com.unipi.msc.spaceroomapi.Controller.Reservation;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.ReservationRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.ReservationPresenter;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.House.HouseService;
import com.unipi.msc.spaceroomapi.Model.Reservation.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationRepository;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.Client;
import com.unipi.msc.spaceroomapi.Model.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final HouseService houseService;
    @PostMapping
    public ResponseEntity<?> reservation(@RequestBody ReservationRequest request) {
        Client client;
        try {
            client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USER_MUST_BE_CLIENT));
        }
        if (request.getHouseId() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.HOUSE_ID_IS_OBLIGATORY));
        if (request.getPrice() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.PRICE_IS_OBLIGATORY));
        if (request.getDate() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.DATE_IS_OBLIGATORY));
        House h = houseService.getHouse(request.getHouseId()).orElse(null);
        if (h == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.HOUSE_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .client(client)
                .house(h)
                .price(request.getPrice())
                .dateFrom(request.getDate().getFrom())
                .dateTo(request.getDate().getTo())
                .status(ReservationStatus.SUCCESS)
                .build();
        reservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(ReservationPresenter.getReservation(reservation));
    }
    @PostMapping("{id}/reject")
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
}
