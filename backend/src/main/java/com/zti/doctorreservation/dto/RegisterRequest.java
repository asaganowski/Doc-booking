package com.zti.doctorreservation.dto;

import com.zti.doctorreservation.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private User.Role role = User.Role.PATIENT;

    // Patient fields
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
