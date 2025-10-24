package org.frogcy.furniturecommon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
public abstract class AuditableEntity extends BaseEntity{

    @CreatedBy
    @Column(updatable = false)
    private Integer createdById;

    @LastModifiedBy
    @Column(insertable = false)
    private Integer updatedById;
}
