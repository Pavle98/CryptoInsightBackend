package com.example.cryptoinsight.dto;

import lombok.Data;

@Data
public class ValueOfEthInUsdResponseDto {
    private String status;
    private String message;
    private ValueOfEthInUsdDto result;
}
