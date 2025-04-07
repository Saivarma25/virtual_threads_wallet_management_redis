package cdc.wallet.management.util;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class FullAuditable extends CreatedAuditable {

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
