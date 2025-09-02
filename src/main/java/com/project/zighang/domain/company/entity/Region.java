package com.project.zighang.domain.company.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Region {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 60, nullable = false)
    private String code;     // e.g. SEOUL_GANGNAM_GU

    @Column(length = 50, nullable = false)
    private String nameKo;   // e.g. 강남구

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;   // 시/도 → 구/군 계층
}
