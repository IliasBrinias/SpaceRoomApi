package com.unipi.msc.spaceroomapi.Model.Image;

import com.unipi.msc.spaceroomapi.Model.House.House;
import com.unipi.msc.spaceroomapi.Shared.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    public Image uploadImage(MultipartFile file) throws IOException {
        return imageRepository.save(Image.builder()
                .name(file.getName())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .build());
    }
    public Image uploadHouseImage(MultipartFile file, House house) throws IOException {
        return imageRepository.save(Image.builder()
                .name(file.getName())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .house(house)
                .build());
    }
    public Image uploadUserImage(MultipartFile file) throws IOException {
        return imageRepository.save(Image.builder()
                .name(file.getName())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes()))
                .build());
    }
    public Optional<Image> findImageById(Long Id){
        return imageRepository.findById(Id);
    }
}
