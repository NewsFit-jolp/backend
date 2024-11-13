package com.example.newsfit.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @Column(updatable = false)
    private ZonedDateTime createdDate;

    private ZonedDateTime lastModifiedDate;

    protected Boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        this.createdDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.lastModifiedDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
