package com.project.zighang.global.prompt.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(
        name = "prompt",
        uniqueConstraints = @UniqueConstraint(name = "uk_tag", columnNames = "tag")
)
public class Prompt extends BaseEntity {

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String tag;
}
