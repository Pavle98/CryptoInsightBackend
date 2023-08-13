package com.example.cryptoinsight.dto;

import lombok.Data;

@Data
public class NumberFromTimestampResponseDto {
    private String jsonrpc;
    private int id;
    private String result;
}
