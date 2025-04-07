package cdc.wallet.management.util;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionKey implements Serializable {

    @Serial
    private static final long serialVersionUID = -2334564426951713233L;

    @Column(name = "wallet_id", nullable = false)
    private Long walletMasterId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

}
