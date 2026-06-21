package com.example.smart_cinema_booking_system.model.dto;

import com.example.smart_cinema_booking_system.model.ENUM.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MovieRequest {

    private Long movieId;

    @NotBlank(message = "Title không được để trống")
    @Size(max = 255, message = "Tối đa 255 ký tự")
    private String title;

    private String description;

    @NotNull(message = "Duration không được null")
    @Min(value = 1, message = "Duration phải > 0")
    private Integer duration;

    @NotNull(message = "Release date không được null")
    private LocalDate releaseDate;

    private String language;

    @NotNull(message = "Status không được null")
    private MovieStatus status;

    @NotEmpty(message = "Phải chọn ít nhất 1 genre")
    private List<Long> genreIds;

    private MultipartFile poster;
}
