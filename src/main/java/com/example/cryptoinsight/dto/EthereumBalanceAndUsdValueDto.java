package com.example.cryptoinsight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EthereumBalanceAndUsdValueDto {
    private double balance;
    private double value;
}
