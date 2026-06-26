package com.example.smart_cinema_booking_system.model.dto;

import com.example.smart_cinema_booking_system.model.ENUM.ShowtimeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ShowtimeRequest {
    private Long showtimeId;

    @NotNull(message = "Vui lòng chọn phim")
    private Long movieId;

    @NotNull(message = "Vui lòng chọn phòng")
    private Long roomId;

    @NotNull(message = "Vui lòng chọn thời gian bắt đầu")
    private LocalDateTime startTime;

    @NotNull(message = "Vui lòng chọn thời gian kết thúc")
    private LocalDateTime endTime;

    @NotNull(message = "Vui lòng nhập giá vé")
    @DecimalMin(value = "0.01", message = "Giá vé phải lớn hơn 0")
    private BigDecimal ticketPrice;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private ShowtimeStatus status;
}
