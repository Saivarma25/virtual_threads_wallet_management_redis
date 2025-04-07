package cdc.wallet.management.model;

import cdc.wallet.management.util.CreatedAuditable;
import cdc.wallet.management.util.WalletTransactionKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet_transaction")
@EqualsAndHashCode(callSuper = true)
public class WalletTransaction extends CreatedAuditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 7602873129025965641L;

    @EmbeddedId
    private WalletTransactionKey walletTransactionId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;

}
