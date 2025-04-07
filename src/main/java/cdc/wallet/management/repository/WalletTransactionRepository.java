package cdc.wallet.management.repository;

import cdc.wallet.management.model.WalletTransaction;
import cdc.wallet.management.util.WalletTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, WalletTransactionKey> {

}
