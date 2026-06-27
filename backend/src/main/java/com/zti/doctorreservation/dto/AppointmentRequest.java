package com.zti.doctorreservation.dto;

import lombok.Data;

@Data
public class AppointmentRequest {
    private Long timeSlotId;
    private String notes;
}
