package cdc.wallet.management.service;

import cdc.wallet.management.model.WalletMaster;
import cdc.wallet.management.model.WalletTransaction;
import cdc.wallet.management.repository.WalletTransactionRepository;
import cdc.wallet.management.util.WalletTransactionKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public void addTransaction(@NotNull BigDecimal amount, @NotNull Long walletMasterId,
                               @NotNull Long clientId, @NotBlank String description) {
        walletTransactionRepository.save(new WalletTransaction(new WalletTransactionKey(walletMasterId,
                System.currentTimeMillis()), clientId, amount, description));
    }
}
