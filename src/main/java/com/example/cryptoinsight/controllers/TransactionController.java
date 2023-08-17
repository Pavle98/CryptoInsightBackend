package com.example.cryptoinsight.controllers;

import com.example.cryptoinsight.dto.*;
import com.example.cryptoinsight.services.EtherscanApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private EtherscanApiService etherscanApiService;

    private static final Logger logger = LogManager.getLogger(TransactionController.class);
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getbalance/{address}")
    public EthereumBalanceAndUsdValueDto getBalance(@PathVariable String address) {

        return etherscanApiService.getBalance(address);
    }
    @GetMapping("/getvalueofeth")
    public Double getValueOfEth(){
        return etherscanApiService.getEthPriceInUsd();
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getnormaltransactions/address={address}&startblock={startBlock}&endblock={endBlock}")
    public List<EthereumTransactionDto> getNormalTransactions(@PathVariable String address, @PathVariable String startBlock, @PathVariable String endBlock){
        logger.info("Fetching normal transactions of this address: " + address + " from block number: " + startBlock +" to block number: " + endBlock);
        return etherscanApiService.getNormalTransactions(address, startBlock, endBlock);
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getinternaltransactions/address={address}&startblock={startBlock}&endblock={endBlock}")
    public List<EthereumTransactionDto>  getInternalTransactions(@PathVariable String address, @PathVariable String startBlock, @PathVariable String endBlock){
        logger.info("Fetching internal transactions of this address: " + address + " from block number: " + startBlock +" to block number: " + endBlock);
        return etherscanApiService.getInternalTransactions(address, startBlock, endBlock);
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/infura/historicalEthBalance/address={address}&timestamp={timeStamp}")
    public double getHistoricalBalance(@PathVariable String address, @PathVariable long timeStamp){
        logger.info("Fetching ETH historical balance for this address: " +address + " and this timestamp: " +timeStamp);
        return etherscanApiService.getHistoricalEthBalance(address, timeStamp);
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getusdttransactions/address={address}&startblock={startBlock}&endblock={endBlock}")
    public List<UsdtTransactionDto> getUsdtTransactions(@PathVariable String address, @PathVariable String startBlock, @PathVariable String endBlock){
        logger.info("Fetching USDT transactions of this address: " + address + " from block number: " + startBlock +" to block number: " + endBlock);
        return etherscanApiService.getUsdtTransactions(address, startBlock, endBlock);
    }

     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getlatesttransactions")
    public List<EthereumTransactionDto> getLatestEthTransactions(){
        logger.info("Fetching ethereum transactions of last block");
        return etherscanApiService.getLatestEthTransactions();
    }

     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getValuesOverPeriodOfTime/ETH/address={address}&startday={startingDayTimestamp}&endday={endDayTimestamp}")
    public List<WalletGraphRecord> getEthBalanceOverPeriodOfTime(@PathVariable String address, @PathVariable long startingDayTimestamp,
                                                      @PathVariable long endDayTimestamp){
        logger.info("Fetching ethereum balance over period of time for this address: " +address + "for these timestapms \n starting timestamp: " +
                startingDayTimestamp +"\n ending timestamp: "  + endDayTimestamp);
        return etherscanApiService.getEthBalanceOverPeriodOfTime(address, startingDayTimestamp, endDayTimestamp);
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getValuesOverPeriodOfTime/USDT/address={address}&startday={startingDayTimestamp}&endday={endDayTimestamp}")
    public List<WalletGraphRecord> getUSDTBalanceOverPeriodOfTime(@PathVariable String address, @PathVariable long startingDayTimestamp,
                                                                 @PathVariable long endDayTimestamp){
        logger.info("Fetching USDT balance over period of time for this address: " +address + "for these timestapms \n starting timestamp: " +
                startingDayTimestamp +"\n ending timestamp: "  + endDayTimestamp);
        return etherscanApiService.getUSDTBalanceOverPeriodOfTime(address, startingDayTimestamp, endDayTimestamp);
    }
     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getusdtbalance/{address}")
    public Double getUsdtBalance(@PathVariable String address){
        logger.info("Fetching USDT balance for this address: " +address);
        return etherscanApiService.getUsdtBalance(address);
    }


     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/infura/historicalUsdtBalance/address={address}&timestamp={timeStamp}")
    public Double getHistoricalUsdtBalance(@PathVariable String address, @PathVariable long timeStamp){
        logger.info("Fetching USDT historical balance for this address: " +address + " and this timestamp: " +timeStamp);
        return etherscanApiService.getHistoricalUsdtBalance(address,timeStamp);
    }

     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/getalltokentransactions/address={address}&startblock={startBlock}&endblock={endBlock}")
    public List<GetAllTokenTransactionDto> getAllTokenTransaction(@PathVariable String address, @PathVariable String startBlock, @PathVariable String endBlock){
        logger.info("Fetching all token transactions of this address: " + address + " from block number: " + startBlock +" to block number: " + endBlock);
        return etherscanApiService.getAllTokenTransactions(address, startBlock, endBlock);
    }

     @CrossOrigin(origins = {"https://cryptoinsight-frontend-8ec4c0098af9.herokuapp.com", "http://localhost:8081"})
    @GetMapping("/dateToBlockNumber/timestamp={timeStamp}")
    public String dateToBlockNumber( @PathVariable long timeStamp){
        return etherscanApiService.getBlockNumberFromTimestamp(timeStamp);
    }




}
