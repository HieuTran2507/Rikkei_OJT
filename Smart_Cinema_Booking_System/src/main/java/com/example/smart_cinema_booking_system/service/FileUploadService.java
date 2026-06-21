package com.example.smart_cinema_booking_system.service;

import com.example.smart_cinema_booking_system.exception.FieldException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads/posters/";

    private static final String DEFAULT_POSTER = "/posters/default.png";

    public String uploadPoster(MultipartFile file) {

        // 1. Nếu không chọn file → dùng default
        if (file == null || file.isEmpty()) {
            return DEFAULT_POSTER;
        }

        // 2. Validate extension
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            throw new FieldException(
                    "poster",
                    "file ảnh không hợp lệ"
            );
        }

        String extension = getExtension(originalFileName);

        if (!isValidImage(extension)) {
            throw new FieldException(
                    "poster",
                    "Chỉ cho phép file jpg, jpeg, png"
            );
        }

        try {

            String fileName = UUID.randomUUID() + "_" + originalFileName;
            Path path = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(path.getParent());
            Files.copy(
                    file.getInputStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/posters/" + fileName;

        } catch (IOException e) {
            throw new FieldException(
                    "poster",
                    "upload ảnh thất bại"
            );
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1)
                .toLowerCase();
    }

    private boolean isValidImage(String extension) {
        return extension.equals("jpg")
                || extension.equals("jpeg")
                || extension.equals("png");
    }
}