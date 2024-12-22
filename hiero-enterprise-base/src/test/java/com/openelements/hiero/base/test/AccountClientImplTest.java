package com.openelements.hiero.base.test;

import com.openelements.hiero.base.implementation.AccountClientImpl;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.openelements.hiero.base.HieroException;
import com.openelements.hiero.base.protocol.AccountBalanceRequest;
import com.openelements.hiero.base.protocol.AccountBalanceResponse;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountClientImplTest {

    private ProtocolLayerClient mockProtocolLayerClient;
    private AccountClientImpl accountClientImpl;

    @BeforeEach
    public void setUp() {
        mockProtocolLayerClient = mock(ProtocolLayerClient.class);
        accountClientImpl = new AccountClientImpl(mockProtocolLayerClient);
    }

    @Test
    public void testGetAccountBalance_ValidPositiveBalance() throws HieroException {
        AccountId accountId = AccountId.fromString("0.0.12345");
        Hbar expectedBalance = new Hbar(10);

        // Mock the response
        AccountBalanceResponse mockResponse = mock(AccountBalanceResponse.class);
        when(mockResponse.hbars()).thenReturn(expectedBalance);

        when(mockProtocolLayerClient.executeAccountBalanceQuery(
                ArgumentMatchers.any(AccountBalanceRequest.class)
        )).thenReturn(mockResponse);

        Hbar balance = accountClientImpl.getAccountBalance(accountId);

        assertEquals(expectedBalance, balance);
    }

    @Test
    public void testGetAccountBalance_ZeroBalance() throws HieroException {
        AccountId accountId = AccountId.fromString("0.0.67890");
        Hbar expectedBalance = new Hbar(0);

        AccountBalanceResponse mockResponse = mock(AccountBalanceResponse.class);
        when(mockResponse.hbars()).thenReturn(expectedBalance);

        when(mockProtocolLayerClient.executeAccountBalanceQuery(
                ArgumentMatchers.any(AccountBalanceRequest.class)
        )).thenReturn(mockResponse);

        Hbar balance = accountClientImpl.getAccountBalance(accountId);

        assertEquals(expectedBalance, balance);
    }

    @Test
    public void testGetAccountBalance_InvalidAccount_ThrowsException() throws HieroException {
        AccountId invalidAccountId = AccountId.fromString("0.0.9999999");

        when(mockProtocolLayerClient.executeAccountBalanceQuery(
                ArgumentMatchers.any(AccountBalanceRequest.class)
        )).thenThrow(new HieroException("Invalid account"));

        assertThrows(HieroException.class, () -> {
            accountClientImpl.getAccountBalance(invalidAccountId);
        });
    }

    @Test
    public void testGetAccountBalance_NullThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            accountClientImpl.getAccountBalance((AccountId) null);
        });
    }

    @Test
    public void testGetAccountBalance_ProtocolLayerClientFails() throws HieroException {
        AccountId accountId = AccountId.fromString("0.0.12345");

        when(mockProtocolLayerClient.executeAccountBalanceQuery(
                ArgumentMatchers.any(AccountBalanceRequest.class)
        )).thenThrow(new RuntimeException("Protocol Layer Failure"));

        assertThrows(RuntimeException.class, () -> {
            accountClientImpl.getAccountBalance(accountId);
        });
    }
}