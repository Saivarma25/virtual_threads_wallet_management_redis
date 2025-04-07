package cdc.wallet.management.model;

import cdc.wallet.management.util.FullAuditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "wallet_master")
@EqualsAndHashCode(callSuper = true)
public class WalletMaster extends FullAuditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 938188764288853868L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_master_id")
    private Long walletMasterId;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotBlank
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @NotNull
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public WalletMaster(Long clientId, String currency, BigDecimal balance, Boolean isActive) {
        this.clientId = clientId;
        this.currency = currency;
        this.balance = balance;
        this.isActive = isActive;
    }

}
