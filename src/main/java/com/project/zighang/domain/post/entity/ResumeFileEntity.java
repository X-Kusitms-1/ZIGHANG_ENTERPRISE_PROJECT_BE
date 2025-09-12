package com.project.zighang.domain.post.entity;

import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeFileEntity extends BaseEntity {

    private String originalFileName;

    private String objectUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_apply_entity_id")
    private PostApplyEntity postApplyEntity;

    public static ResumeFileEntity create(
            String originalFileName, String objectUrl, PostApplyEntity postApplyEntity
    ) {
        return new ResumeFileEntity(
                originalFileName, objectUrl, postApplyEntity
        );
    }
}
