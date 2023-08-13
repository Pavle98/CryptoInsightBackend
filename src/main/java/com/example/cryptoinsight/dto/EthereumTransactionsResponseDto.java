package com.example.cryptoinsight.dto;


import lombok.Data;

import java.util.List;

@Data
public class EthereumTransactionsResponseDto {
        private String status;
        private String message;
        private List<EthereumTransactionDto> result;

}