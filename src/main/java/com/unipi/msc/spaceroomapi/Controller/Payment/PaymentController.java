package com.unipi.msc.spaceroomapi.Controller.Payment;

import com.stripe.exception.StripeException;
import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Payment.Request.PaymentRequest;
import com.unipi.msc.spaceroomapi.Controller.Payment.Request.PaymentResultRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Model.House.HouseService;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationRepository;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final HouseService houseService;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    @PostMapping
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request){
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
                .status(ReservationStatus.PENDING)
                .build();
        reservationRepository.save(reservation);

        Stripe.apiKey = "sk_test_K4dDnnwpTYqthU0VnbqULyZ000I8mYOPJY";
        String YOUR_DOMAIN = "http://localhost:4242";
        SessionCreateParams params =SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(YOUR_DOMAIN + "/success.html")
            .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
            .addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
                    .setPrice(String.valueOf(request.getPrice()))
                .build())
            .build();
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.PLEASE_TRY_AGAIN_LATER));
        }
        Map<String,String> map = new HashMap<>();
        map.put("url",session.getUrl());
        return ResponseEntity.ok(map);
    }
    @PostMapping("/result")
    public ResponseEntity<?> result(@RequestBody PaymentResultRequest request) {
        Client client;
        try {
            client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }catch (ClassCastException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.USER_MUST_BE_CLIENT));
        }
        if (request.getReservationId() == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.RESERVATION_ID_IS_OBLIGATORY));
        ReservationStatus status;
        try {
            status = ReservationStatus.valueOf(request.getStatus().toUpperCase());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.STATUS_NOT_VALID));
        }
        Reservation reservation = reservationService.getReservationWithId(request.getReservationId()).orElse(null);
        if (reservation == null) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.RESERVATION_NOT_FOUND));
        if (!Objects.equals(reservation.getClient().getId(), client.getId())) return ResponseEntity.badRequest().body(new ErrorResponse(false,ErrorMessages.RESERVATION_IS_NOT_FOR_THIS_CLIENT));
        reservation.setStatus(status);
        reservationRepository.save(reservation);
        return ResponseEntity.ok().build();
    }
}
