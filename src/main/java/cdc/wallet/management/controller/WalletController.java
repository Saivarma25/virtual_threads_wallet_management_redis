package cdc.wallet.management.controller;

import cdc.wallet.management.model.WalletMaster;
import cdc.wallet.management.service.WalletMasterService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletMasterService walletMasterService;

    public WalletController(WalletMasterService walletMasterService) {
        this.walletMasterService = walletMasterService;
    }

    @GetMapping("/getBalance")
    public ResponseEntity<Object> getWalletBalance(@RequestParam @NotNull Long walletId,
                                                   @RequestParam @NotNull Long clientId) {
        WalletMaster walletMaster = walletMasterService.getWalletMasterBalance(walletId, clientId);
        if (walletMaster == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No wallet found with given details");
        else
            return ResponseEntity.ok(walletMaster);
    }

    @PostMapping("/createWallet")
    public ResponseEntity<Object> createWallet(@RequestParam @NotNull Long clientId,
                                               @RequestParam @NotBlank String currency) {
        WalletMaster walletMaster = walletMasterService.createWalletMaster(clientId, currency);
        if (walletMaster == null)
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Failed to create a wallet");
        else
            return ResponseEntity.ok(walletMaster);
    }

    @PutMapping("/addTransaction")
    public ResponseEntity<Object> addWalletTransaction(@RequestParam @NotNull Long walletId,
                                                       @RequestParam @NotNull Long clientId,
                                                       @RequestParam @NotNull BigDecimal amount,
                                                       @RequestParam @NotBlank String description) {
        WalletMaster walletMaster = walletMasterService.getWalletMasterBalance(walletId, clientId);
        if (walletMaster == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No wallet found with given details");

        return ResponseEntity.ok(walletMasterService.updateWalletAmount(walletMaster, amount, description));
    }

}
