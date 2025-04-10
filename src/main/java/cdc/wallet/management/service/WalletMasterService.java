package cdc.wallet.management.service;

import cdc.wallet.management.dto.WalletDTO;
import cdc.wallet.management.model.WalletMaster;
import cdc.wallet.management.repository.WalletMasterRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletMasterService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletMasterRepository walletMasterRepository;

    private final WalletTransactionService walletTransactionService;

    private final RedisTemplate<String, BigDecimal> redisTemplate;

    private WalletMasterService self;

    private static final String WALLET = "wallet:";

    public WalletMasterService(WalletMasterRepository walletMasterRepository,
                               WalletTransactionService walletTransactionService,
                               RedisTemplate<String, BigDecimal> redisTemplate) {
        this.walletMasterRepository = walletMasterRepository;
        this.walletTransactionService = walletTransactionService;
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setSelf(@Lazy WalletMasterService walletMasterService) {
        this.self = walletMasterService;
        logger.info("Creating a self object for waller master service");
    }

    /**
     * Method to get the wallet balance with the given wallet id
     * @param walletMasterId ID of the wallet to fetch record from cache or db
     * @return WalletMaster object that has wallet details along with balance
     */
    @Transactional
    public WalletMaster getWalletMasterBalance(@NotNull Long walletMasterId) {
        BigDecimal cachedBalance = redisTemplate.opsForValue().get(WALLET + walletMasterId);
        if (cachedBalance != null)
            return new WalletMaster(walletMasterId, cachedBalance);

        WalletMaster walletMaster = walletMasterRepository.findByWalletMasterId(walletMasterId);
        if (walletMaster != null)
            redisTemplate.opsForValue().set(WALLET + walletMaster.getWalletMasterId(), walletMaster.getBalance());
        return walletMaster;
    }

    /**
     * Method to create a wallet with given details
     * @param clientId ID of client to use in wallet creation
     * @param currency Currency that wallet to be created holds
     * @return WalletMaster object which is created with given details
     */
    public WalletMaster createWalletMaster(@NotNull Long clientId, @NotBlank String currency) {
        WalletMaster walletMaster = walletMasterRepository.save(new WalletMaster(
                clientId, currency, new BigDecimal(0), true));
        redisTemplate.opsForValue().set(WALLET + walletMaster.getWalletMasterId(), walletMaster.getBalance());
        return walletMaster;
    }

    /**
     * Method to update wallet balance in DB, which will be called by a virtual thread
     * @param walletMaster existing WalletMaster Object to update
     * @param amountToAdd Amount that needs to be added to the given WalletMaster object
     */
    public void updateWalletMaster(WalletMaster walletMaster, BigDecimal amountToAdd) {
        walletMaster.setBalance(walletMaster.getBalance().add(amountToAdd));
        walletMasterRepository.save(walletMaster);
    }

    /**
     * Method to createATransaction which can be negative or positive
     * @param walletDTO dto that holds basic details to make a transaction
     * @return WalletDTO object that has updated details after transaction
     */
    public WalletDTO createTransactions(@NotNull WalletDTO walletDTO) {
        WalletMaster walletMaster = self.getWalletMasterBalance(walletDTO.getWalletMasterId());
        if (walletMaster == null) return null;

        BigDecimal newAmount = walletMaster.getBalance().add(walletDTO.getAmount());
        if (newAmount.signum() >= 0)
            redisTemplate.opsForValue().increment(WALLET + walletMaster.getWalletMasterId(), walletDTO.getAmount().doubleValue());

        String description = walletDTO.getDescription();
        BigDecimal amount = walletDTO.getAmount();
        Thread.startVirtualThread(() -> self.processWalletTransactions(
                amount, newAmount, walletMaster.getWalletMasterId(), description));
        walletDTO.setAmount(newAmount);
        return walletDTO;
    }

    /**
     * Method to process transaction which will be running in a virtual thread for high throughput
     * @param amount Amount of the transaction being handled
     * @param newAmount Amount which is sum of existing and transaction amount
     * @param walletMasterId wallet id to use in transaction object creation
     * @param description details of why this transaction is being happened
     */
    @Transactional
    public void processWalletTransactions(BigDecimal amount, BigDecimal newAmount, Long walletMasterId, String description) {
        WalletMaster walletMasterDB = walletMasterRepository.findByWalletMasterId(walletMasterId);
        walletTransactionService.addTransaction(amount, walletMasterId, description);
        if (newAmount.signum() < 0)
            walletTransactionService.addTransaction(amount.abs(), walletMasterId, "Reversal");
        else
            self.updateWalletMaster(walletMasterDB, amount);
    }

}