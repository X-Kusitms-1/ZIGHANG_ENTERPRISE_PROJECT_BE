package com.project.zighang.domain.user.entity;

import com.project.zighang.domain.user.dto.request.AccuracyRequest;
import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Accuracy extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    private String question1;

    private String question2;

    private String question3;

    private String question4;

    private String question5;

    private String question6;

    private String question7;

    private String question8;

    private String question9;

    private String question10;

    public static Accuracy create(UserEntity userEntity, AccuracyRequest.answers answers) {
        return Accuracy.builder()
                .userEntity(userEntity)
                .question1(answers.question1())
                .question2(answers.question2())
                .question3(answers.question3())
                .question4(answers.question4())
                .question5(answers.question5())
                .question6(answers.question6())
                .question7(answers.question7())
                .question8(answers.question8())
                .question9(answers.question9())
                .question10(answers.question10())
                .build();
    }

    public void update(AccuracyRequest.answers answers) {
        this.question1 = answers.question1();
        this.question2 = answers.question2();
        this.question3 = answers.question3();
        this.question4 = answers.question4();
        this.question5 = answers.question5();
        this.question6 = answers.question6();
        this.question7 = answers.question7();
        this.question8 = answers.question8();
        this.question9 = answers.question9();
        this.question10 = answers.question10();
    }
}
