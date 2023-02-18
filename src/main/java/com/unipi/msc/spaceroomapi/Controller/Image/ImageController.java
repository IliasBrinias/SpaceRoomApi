package com.unipi.msc.spaceroomapi.Controller.Image;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Controller.Responses.ErrorResponse;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.Image.ImageService;
import com.unipi.msc.spaceroomapi.Shared.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;
    @GetMapping("{id}")
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        Image image = imageService.findImageById(id).orElse(null);
        if (image==null) return ResponseEntity.badRequest().body(new ErrorResponse(false, ErrorMessages.IMAGE_NOT_FOUND));
        byte[] img = ImageUtils.decompressImage(image.getImageData());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(img);
    }
}
