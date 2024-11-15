package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorImplTest {

    @Mock
    AccountService accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    @Test
    public void testTransfer() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setType(0);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setType(0);

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement argument) {
                return argument != null && argument.getId() == 1L;
            }
        }))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement argument) {
                return argument != null && argument.getId() == 2L;
            }
        }))).thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE);

    }

    @Test
    public void testTransferWithComission() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(1000));
        sourceAccount.setType(0);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(0));
        destinationAccount.setType(0);

        BigDecimal amount = new BigDecimal(100);
        BigDecimal comissionPercent = new BigDecimal(5);

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement testAgreement) {
                return testAgreement != null && testAgreement.getId() == 1L;
            }
        }))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement testAgreement) {
                return testAgreement != null && testAgreement.getId() == 2L;
            }
        }))).thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement, sourceAccount.getType(),
                destinationAccount.getType(), amount, comissionPercent);
        verify(accountService).charge(sourceAccount.getId(), amount.negate().multiply(comissionPercent));
        verify(accountService).makeTransfer(sourceAccount.getId(), destinationAccount.getId(), amount);
    }
}
