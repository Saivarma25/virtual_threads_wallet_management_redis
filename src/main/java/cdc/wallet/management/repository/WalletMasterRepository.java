package cdc.wallet.management.repository;

import cdc.wallet.management.model.WalletMaster;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletMasterRepository extends JpaRepository<WalletMaster, Long> {

    WalletMaster findByWalletMasterIdAndClientId(@NotNull Long walletId, @NotNull Long clientId);

}
