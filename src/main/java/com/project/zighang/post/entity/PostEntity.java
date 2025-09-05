package com.project.zighang.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_postings")
@Getter
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long recruitmentId;

    @Lob
    @Column(name = "recruitment_original_url", length = 65535)
    private String recruitmentOriginalUrl;

    @Column(name = "uuid", unique = true, nullable = false, length = 36)
    private String uuid;

    @Lob
    @Column(name = "ocr_data", length = 16777215)
    private String ocrData;

    @Lob
    @Column(name = "summary_data", length = 65535)
    private String summaryData;

    @Column(name = "title", length = 512)
    private String title;

    @Lob
    @Column(name = "content", length = 2147483647)
    private String content;

    @Column(name = "score")
    private Integer score;

    @Column(name = "recruitment_region", length = 255)
    private String recruitmentRegion;

    @Column(name = "recruitment_address", length = 512)
    private String recruitmentAddress;

    @Column(name = "affiliate", length = 255)
    private String affiliate;

    @Column(name = "is_view")
    private Boolean isView = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Lob
    @Column(name = "recruitment_image_url", length = 65535) // TEXT
    private String recruitmentImageUrl;

    @Column(name = "min_career")
    private Integer minCareer;

    @Column(name = "max_career")
    private Integer maxCareer;

    @Column(name = "education", length = 255)
    private String education;

    @Column(name = "industry", length = 255)
    private String industry;

    @Column(name = "recruitment_start_date", length = 100)
    private String recruitmentStartDate;

    @Column(name = "recruitment_end_date", length = 100)
    private String recruitmentEndDate;

    @Column(name = "upload_date", length = 100)
    private String uploadDate;

    @Column(name = "updated_at", length = 100)
    private String updatedAt;

    @Column(name = "recruitment_deadline_type", length = 255)
    private String recruitmentDeadlineType;

    @Column(name = "recruitment_type", length = 255)
    private String recruitmentType;

    @Lob
    @Column(name = "depth_one", length = 65535)
    private String depthOne;

    @Lob
    @Column(name = "depth_two", length = 65535)
    private String depthTwo;

    @Column(name = "expired_time", length = 100)
    private String expiredTime;
}