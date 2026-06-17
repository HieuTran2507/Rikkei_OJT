package com.example.smart_cinema_booking_system.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(
            min = 4,
            max = 50,
            message = "Username phải từ 4-50 ký tự"
    )
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(
            min = 6,
            max = 100,
            message = "Password ít nhất 6 ký tự"
    )
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng cú pháp")
    private String email;

    @NotBlank(message = "Tên không được để trống")
    @Size(
            max = 100,
            message = "Tên không được quá 100 ký tự"
    )
    private String fullName;

    @Pattern(
            regexp = "^0\\d{9,10}$",
            message = "Số điện thoại không tồn tại"
    )
    private String phone;

    @Size(
            max = 255,
            message = "địa chỉ không quá 255 ký tự"
    )
    private String address;
}
