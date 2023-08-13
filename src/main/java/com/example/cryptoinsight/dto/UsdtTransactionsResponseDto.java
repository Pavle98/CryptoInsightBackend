package com.example.cryptoinsight.dto;


import lombok.Data;

import java.util.List;

@Data
public class UsdtTransactionsResponseDto {
    private String status;
    private String message;
    private List<UsdtTransactionDto> result;

}

