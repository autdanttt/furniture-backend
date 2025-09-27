package org.frogcy.furniturecommon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;

    @CreatedBy
    @Column(updatable = false)
    private Integer createdById;

    @LastModifiedDate
    @Column(insertable = false)
    private Date updatedAt;

    @LastModifiedBy
    @Column(insertable = false)
    private Integer updatedById;

    // Soft delete
    private Boolean deleted = false;

    private Date deletedAt;

    private Integer deletedById;
}