package com.codingshuttle.youtube.hospitalManagement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SignupResponseDto {

    private Long id;

    private String username;
}
