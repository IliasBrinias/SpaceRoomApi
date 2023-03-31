package com.unipi.msc.spaceroomapi.Controller.Message;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Request.MessageRequest;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Controller.Responses.MessagePresenter;
import com.unipi.msc.spaceroomapi.Model.Enum.ReservationStatus;
import com.unipi.msc.spaceroomapi.Model.Message.Message;
import com.unipi.msc.spaceroomapi.Model.Message.MessageRepository;
import com.unipi.msc.spaceroomapi.Model.Message.MessageService;
import com.unipi.msc.spaceroomapi.Model.Reservation.Reservation;
import com.unipi.msc.spaceroomapi.Model.Reservation.ReservationService;
import com.unipi.msc.spaceroomapi.Model.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ReservationService reservationService;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    @GetMapping("reservation/{id}/message")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationWithId(id).orElse(null);
        if (reservation==null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_NOT_FOUND));
        if (reservation.getStatus()!= ReservationStatus.SUCCESS) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_IS_NOT_APPROVED));
        List<Message> messages = messageService.getMessagesForReservation(reservation);
        if (messages == null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NO_MESSAGES_FOUND));
        List<MessagePresenter> presenter = new ArrayList<>();
        messages.forEach(msg->presenter.add(MessagePresenter.getPresenter(msg)));
        return ResponseEntity.ok(presenter);
    }
    @PostMapping("reservation/{id}/message")
    public ResponseEntity<?> addMessage(@PathVariable Long id, @RequestBody MessageRequest request) {
        Reservation reservation = reservationService.getReservationWithId(id).orElse(null);
        if (reservation==null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_NOT_FOUND));
        if (reservation.getStatus()!= ReservationStatus.SUCCESS) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.RESERVATION_IS_NOT_APPROVED));
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(u.getId(), reservation.getHouse().getHost().getId()) && !Objects.equals(u.getId(), reservation.getClient().getId())){
            return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.NOT_AUTHORIZED));
        }
        Message message = messageRepository.save(Message.builder()
                .sender(u)
                .msg(request.getMsg())
                .date(request.getDate())
                .reservation(reservation)
                .build());
        return ResponseEntity.ok(MessagePresenter.getPresenter(message));
    }
}
