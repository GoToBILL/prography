package com.example.prography_project.init.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InitRequestDto {
    private int seed;
    private int quantity;
}