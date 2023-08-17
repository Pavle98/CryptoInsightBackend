package com.example.cryptoinsight.services;

import com.example.cryptoinsight.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class EtherscanApiService {
    private final String ETHERSCAN_KEY = "MF9DENG9WDWVW5ABXNBGUNS4BU1Q9P1C7K";
    private final  String BASE_URL = "https://api.etherscan.io/";
    private final String INFURA_URL = "https://mainnet.infura.io/v3/7e89b90ac2cb4ef6ae5d95bca69574da";
    private final HashMap<String, String> methodIDs = new HashMap<>();
    private final String USDT_CONTRACT_ADDRESS = "0xdAC17F958D2ee523a2206206994597C13D831ec7";
    private final RestTemplate restTemplate;


    public EtherscanApiService() {
        this.restTemplate = new RestTemplate();
    }
    @PostConstruct
    public void init() {
        methodIDs.put("0x88316456", "mint((address,address,uint24,int24,int24,uint256,uint256,uint256,uint256,address,uint256))");
        methodIDs.put("0x791ac947", "swapExactTokensForETHSupportingFeeOnTransferTokens(uint256,address[],address,uint256)");
        methodIDs.put("0x9ac84414", "sweepEth()");
        methodIDs.put("0x78e111f6", "executeFFsYo(address,bytes)");
        methodIDs.put("0xb6f9de95", "swapExactETHForTokensSupportingFeeOnTransferTokens(uint256,address[],address,uint256)");
        methodIDs.put("0x18160ddd", "totalSupply()");
        methodIDs.put("0x70a08231", "balanceOf(address)");
        methodIDs.put("0xa9059cbb", "transfer(address,uint256)");
        methodIDs.put("0x24856bc3", "execute(bytes,bytes[])");
        methodIDs.put("0x3593564c", "execute(bytes,bytes[],uint256)");
        methodIDs.put("0x23b872dd", "transferFrom(address,address,uint256)");
        methodIDs.put("0x095ea7b3", "approve(address,uint256)");
        methodIDs.put("0xdd62ed3e", "allowance(address,address)");
        methodIDs.put("0x7ff36ab5", "swapExactETHForTokens(uint256,address[],address,uint256)");
        methodIDs.put("0xd9627aa4", "sellToUniswap(address[],uint256,uint256,bool)\t");
        methodIDs.put("0xfb3bdb41", "swapETHForExactTokens(uint256,address[],address,uint256)");
        methodIDs.put("0x0502b1c5", "unoswap(address,uint256,uint256,uint256[])");
    }
    public EthereumBalanceAndUsdValueDto getBalance(String address) {
        String url = BASE_URL + "api?module=account&action=balance&address=" + address + "&tag=latest&apikey=" + ETHERSCAN_KEY;
        if (!address.matches("^0x[a-fA-F0-9]{40}$")) {
            return new EthereumBalanceAndUsdValueDto(-1,-1);
        }
        ResponseEntity<BalanceResponseDto> responseEntity = restTemplate.getForEntity(url, BalanceResponseDto.class);
        BalanceResponseDto response = responseEntity.getBody();
        double ethToUsd = getEthPriceInUsd();
        Double balance = formatEthValue(response.getResult());
        return new EthereumBalanceAndUsdValueDto(balance, balance*ethToUsd);
    }
    public List<WalletGraphRecord> getEthBalanceOverPeriodOfTime(String address, long startingDayTimestamp, long endDayTimestamp){
        List<WalletGraphRecord> valuesOverTime = new ArrayList<>();
        int secondsInDay = 86400;
        while(startingDayTimestamp <= endDayTimestamp){
            double currentValue = getHistoricalEthBalance(address,startingDayTimestamp);
            valuesOverTime.add(new WalletGraphRecord(startingDayTimestamp, currentValue));
            startingDayTimestamp+= secondsInDay;
        }
        return valuesOverTime;
    }
    public List<WalletGraphRecord> getUSDTBalanceOverPeriodOfTime(String address, long startingDayTimestamp, long endDayTimestamp){
        List<WalletGraphRecord> valuesOverTime = new ArrayList<>();
        int secondsInDay = 86400;
        while(startingDayTimestamp <= endDayTimestamp){
            double currentValue = getHistoricalUsdtBalance(address,startingDayTimestamp);
            valuesOverTime.add(new WalletGraphRecord(startingDayTimestamp, currentValue));
            startingDayTimestamp+= secondsInDay;
        }
        return valuesOverTime;
    }

    public double getHistoricalEthBalance(String address, long timestamp){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        long currentTime = System.currentTimeMillis();
        if(timestamp*1000 > currentTime){
            //return -2 if date is in the future.
            return -2;
        }
        String blockNumber = getBlockNumberFromTimestamp(timestamp);
        System.out.println(blockNumber + " block number");
        if (blockNumber == "You choose time before first Ethereum transaction was created") {
            return -1;
        }
        JSONObject json = new JSONObject();
        json.put("jsonrpc", "2.0");
        json.put("method", "eth_getBalance");

        JSONArray params = new JSONArray();
        params.put(address);
        params.put(blockNumber);

        json.put("params", params);
        json.put("id", 1);

        String requestBody = json.toString();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        HistoryBalanceDto response = restTemplate.postForObject(INFURA_URL, entity, HistoryBalanceDto.class);
        System.out.println(response);
            String ethBalanceRaw = response.getResult();
            String ethBalance = convertHexToDecimal(ethBalanceRaw);
            return formatEthValue(ethBalance);


    }
    public Double getHistoricalUsdtBalance(String address, long timestamp){
        HttpHeaders headers = new HttpHeaders();
        long currentTime = System.currentTimeMillis();
        if(timestamp*1000 > currentTime){
            return -1.0;
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        String functionSignature = "0x70a08231000000000000000000000000";
        String blockNumber = getBlockNumberFromTimestamp(timestamp);
        JSONObject requestObject = new JSONObject();
        requestObject.put("jsonrpc", "2.0");
        requestObject.put("method", "eth_call");

        JSONObject params = new JSONObject();
        params.put("to", USDT_CONTRACT_ADDRESS);
        params.put("data", functionSignature + address.substring(2));

        requestObject.put("params", new Object[]{params, blockNumber});
        requestObject.put("id", 1);

        String requestBody = requestObject.toString();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        HistoryBalanceDto response = restTemplate.postForObject(INFURA_URL, entity, HistoryBalanceDto.class);

        String decimalValueOfHex = convertHexToDecimal(response.getResult());
        Double usdtBalance = formatUsdtValue(decimalValueOfHex);
        return usdtBalance;
    }

    public List<EthereumTransactionDto> getNormalTransactions(String address, String startBlock, String endBlock) {
        System.out.println("we in normal trans");
        System.out.println(startBlock +" "+ endBlock);
        String url = BASE_URL + "api?module=account&action=txlist&address=" + address +
                "&startblock=" + startBlock + "&endblock=" + endBlock + "&sort=desc" + "&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<EthereumTransactionsResponseDto> responseEntity = restTemplate.getForEntity(url, EthereumTransactionsResponseDto.class);
        EthereumTransactionsResponseDto response = responseEntity.getBody();

        List<EthereumTransactionDto> transactions = response.getResult();

        for(EthereumTransactionDto transaction : transactions){
            double convertedValue = formatEthValue(transaction.getValue());
            transaction.setValue(String.valueOf(convertedValue));
            long timeStampLong = Long.parseLong(transaction.getTimeStamp());
            transaction.setTimeStamp(convertTimestampToDate(timeStampLong));
        }

        return transactions;
    }

    public List<EthereumTransactionDto> getInternalTransactions(String address, String startBlock, String endBlock) {
        String url = BASE_URL + "api?module=account&action=txlistinternal&address=" + address +
                "&startblock=" + startBlock + "&endblock=" + endBlock + "&sort=desc" + "&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<EthereumTransactionsResponseDto> responseEntity = restTemplate.getForEntity(url, EthereumTransactionsResponseDto.class);
        EthereumTransactionsResponseDto response = responseEntity.getBody();
        List<EthereumTransactionDto> transactions = response.getResult();
        for(EthereumTransactionDto transaction : transactions){
            double convertedValue = formatEthValue(transaction.getValue());
            long timeStampLong = Long.parseLong(transaction.getTimeStamp());
            transaction.setValue(String.valueOf(convertedValue));
            transaction.setTimeStamp(convertTimestampToDate(timeStampLong));
        }
        return response.getResult();
    }
    public Double getUsdtBalance(String address){
        String url = BASE_URL + "api?module=account&action=tokenbalance&contractaddress=" + USDT_CONTRACT_ADDRESS
                + "&address=" + address + "&tag=latest&apikey=" + ETHERSCAN_KEY;
        if (!address.matches("^0x[a-fA-F0-9]{40}$")) {
            return -1.0;
        }
        ResponseEntity<BalanceResponseDto> responseEntity = restTemplate.getForEntity(url, BalanceResponseDto.class);
        BalanceResponseDto response = responseEntity.getBody();
        double balance = formatUsdtValue(response.getResult());

        return balance;
    }

    public List<UsdtTransactionDto> getUsdtTransactions(String address, String startBlock, String endBlock) {
        String url = BASE_URL + "api?module=account&action=tokentx&address=" + address
                + "&contractaddress=" + USDT_CONTRACT_ADDRESS +
                "&startblock=" + startBlock + "&endblock=" + endBlock + "&sort=desc" + "&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<UsdtTransactionsResponseDto> responseEntity = restTemplate.getForEntity(url, UsdtTransactionsResponseDto.class);
        UsdtTransactionsResponseDto response = responseEntity.getBody();
        List<UsdtTransactionDto> transactions = response.getResult();
        for(UsdtTransactionDto transaction : transactions){
            double convertedValue = formatUsdtValue(transaction.getValue());
            long timeStampLong = Long.parseLong(transaction.getTimeStamp());

            transaction.setValue(String.valueOf(convertedValue));
            transaction.setTimeStamp(convertTimestampToDate(timeStampLong));
        }
        return transactions;
    }
    public List<GetAllTokenTransactionDto> getAllTokenTransactions(String address, String startBlock, String endBlock) {
        String url = BASE_URL + "api?module=account&action=tokentx&address=" + address +
                "&startblock=" + startBlock + "&endblock=" + endBlock + "&sort=desc" + "&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<GetAllTokenTransactionsResponseDto> responseEntity = restTemplate.getForEntity(url, GetAllTokenTransactionsResponseDto.class);
        GetAllTokenTransactionsResponseDto response = responseEntity.getBody();
        List<GetAllTokenTransactionDto> transactions = response.getResult();
        for(GetAllTokenTransactionDto transaction : transactions){
            double convertedValue = formatUsdtValue(transaction.getValue());
            long timeStampLong = Long.parseLong(transaction.getTimeStamp());
            transaction.setValue(String.valueOf(convertedValue));
            transaction.setTimeStamp(convertTimestampToDate(timeStampLong));
        }
        return response.getResult();
    }
    public List<EthereumTransactionDto>  getLatestEthTransactions(){
        String url = BASE_URL + "api?module=proxy&action=eth_getBlockByNumber&tag=latest&boolean=true&apikey=" +ETHERSCAN_KEY;
        ResponseEntity<LatestTransactionsDto> responseEntity = restTemplate.getForEntity(url, LatestTransactionsDto.class);
        LatestTransactionsDto response = responseEntity.getBody();
        List<EthereumTransactionDto> latestTransactions = response.getResult().getTransactions();
        for(EthereumTransactionDto transaction : latestTransactions){
            transaction.setTimeStamp(convertHexToDecimal(response.getResult().getTimestamp()));
            long timeStampLong = Long.parseLong(transaction.getTimeStamp());
            transaction.setTimeStamp(convertTimestampToDate(timeStampLong));
            String convertedHexValue = convertHexToDecimal(transaction.getValue());
            String convertedDecimalValue = String.valueOf(formatEthValue(convertedHexValue));
            transaction.setValue(convertedDecimalValue);
            if(transaction.getInput().equals("0x")){
                transaction.setMethodId("0x");
                transaction.setFunctionName("P2P exchange");
            }else{
                try {
                    transaction.setMethodId(transaction.getInput().substring(0, 10));
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(methodIDs.containsKey(transaction.getMethodId())){
                    transaction.setFunctionName(methodIDs.get(transaction.getMethodId()));
                }
            }
        }
        return latestTransactions;
    }






    public String getBlockNumberFromTimestamp(long timestamp) {
        String url = BASE_URL + "api?module=block&action=getblocknobytime&timestamp=" + timestamp +
                "&closest=before" + "&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<NumberFromTimestampResponseDto> responseEntity = restTemplate.getForEntity(url, NumberFromTimestampResponseDto.class);
        NumberFromTimestampResponseDto response = responseEntity.getBody();

        try {
            long numberOfBlock = Long.parseLong(response.getResult());
            String blockNumber = "0x" + Long.toHexString(numberOfBlock);
            System.out.println(blockNumber);
            return blockNumber;
        }catch (NumberFormatException numberFormatException){
            return "You choose time before first Ethereum transaction was created";
        }



    }

    private Double formatEthValue(String ethValue) {
        String decimalStr;
        final int decimalFraction = 18;

        if (ethValue.length() > decimalFraction) {
            decimalStr = new StringBuilder(ethValue)
                    .insert(ethValue.length() - decimalFraction, '.')
                    .toString();
        } else {
            decimalStr = String.format("0.%018d", Long.parseLong(ethValue));
        }
        return Double.parseDouble(decimalStr);
    }

    private Double formatUsdtValue(String usdtValue) {
        String decimalStr;
        final int decimalFraction = 6;

        if (usdtValue.length() > decimalFraction) {
            decimalStr = new StringBuilder(usdtValue)
                    .insert(usdtValue.length() - decimalFraction, '.')
                    .toString();
        } else {
            decimalStr = String.format("0.%06d", Long.parseLong(usdtValue));
        }
        return Double.parseDouble(decimalStr);
    }
    private String convertHexToDecimal(String hex) {
        BigInteger bigIntValue = new BigInteger(hex.substring(2), 16);
        return bigIntValue.toString();
    }
    public double getEthPriceInUsd()   {
        String url = BASE_URL + "api?module=stats&action=ethprice" +"&apikey=" + ETHERSCAN_KEY;
        ResponseEntity<ValueOfEthInUsdResponseDto> responseEntity = restTemplate.getForEntity(url, ValueOfEthInUsdResponseDto.class);
        ValueOfEthInUsdResponseDto response = responseEntity.getBody();


        return response.getResult().getEthusd();

    }

    public String convertTimestampToDate(long timestamp){
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        return formattedDate;

    }





}
