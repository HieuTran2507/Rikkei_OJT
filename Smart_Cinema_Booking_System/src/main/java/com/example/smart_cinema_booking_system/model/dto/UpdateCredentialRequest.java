package com.example.smart_cinema_booking_system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCredentialRequest {

    @NotBlank(message = "Username hiện tại không được để trống")
    private String currentUsername;

    @NotBlank(message = "Password hiện tại không được để trống")
    private String currentPassword;

    @NotBlank(message = "Username mới không được để trống")
    @Size(
            min = 4,
            max = 50,
            message = "Username mới phải từ 4-50 ký tự"
    )
    private String newUsername;

    @NotBlank(message = "Password mới không được để trống")
    @Size(
            min = 6,
            max = 100,
            message = "Password mới phải từ 6 ký tự trở lên"
    )
    private String newPassword;
}
