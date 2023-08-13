package com.example.cryptoinsight.dto;

import lombok.Data;

@Data
public class LatestTransactionsDto {
    private String jsonrpc;
    private int id;
    private LastBlockDto result;
}
