package cdc.wallet.management.service;

import cdc.wallet.management.model.WalletMaster;
import cdc.wallet.management.repository.WalletMasterRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletMasterService {

    private final WalletMasterRepository walletMasterRepository;

    private final WalletTransactionService walletTransactionService;

    private static final String WALLET = "wallet";

    public WalletMasterService(WalletMasterRepository walletMasterRepository,
                               WalletTransactionService walletTransactionService) {
        this.walletMasterRepository = walletMasterRepository;
        this.walletTransactionService = walletTransactionService;
    }

    @Cacheable(value = WALLET, key = "#walletId")
    public WalletMaster getWalletMasterBalance(@NotNull Long walletId, @NotNull Long clientId) {
        System.out.println("Making a disk operation because wallet not found in cache");
        return walletMasterRepository.findByWalletMasterIdAndClientId(walletId, clientId);
    }

    @CachePut(value = WALLET, key = "#result.walletMasterId")
    public WalletMaster createWalletMaster(@NotNull Long clientId, @NotBlank String currency) {
        return walletMasterRepository.save(new WalletMaster(clientId, currency, new BigDecimal(0), true));
    }

    @Transactional
    @CachePut(value = WALLET, key = "#result.walletMasterId")
    public WalletMaster updateWalletAmount(@NotNull WalletMaster walletMaster,
                                           @NotNull BigDecimal amount, @NotBlank String description) {
        walletMaster.setBalance(walletMaster.getBalance().add(amount));
        walletTransactionService.addTransaction(amount, walletMaster.getWalletMasterId(),
                walletMaster.getClientId(), description);
        return walletMasterRepository.saveAndFlush(walletMaster);
    }
}
