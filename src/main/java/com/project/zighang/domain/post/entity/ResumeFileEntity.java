package com.project.zighang.domain.post.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ResumeFileEntity extends BaseEntity {

    private String originalFileName;
    private String objectUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_entity_id")
    private ApplicationEntity application;
}
