package com.project.zighang.domain.user.entity;

import com.project.zighang.domain.user.dto.request.AccuracyRequest;
import com.project.zighang.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Accuracy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question1", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question1 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question2", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question2 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question3", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question3 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question4", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question4 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question5", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question5 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question6", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question6 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question7", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question7 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question8", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question8 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question9", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question9 = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "accuracy_question10", joinColumns = @JoinColumn(name = "accuracy_id"))
    @Column(name = "answer")
    private Set<String> question10 = new HashSet<>();


    public static Accuracy create(UserEntity userEntity, AccuracyRequest.answers answers) {
        return Accuracy.builder()
                .userEntity(userEntity)
                .question1(new HashSet<>(answers.question1()))
                .question2(new HashSet<>(answers.question2()))
                .question3(new HashSet<>(answers.question3()))
                .question4(new HashSet<>(answers.question4()))
                .question5(new HashSet<>(answers.question5()))
                .question6(new HashSet<>(answers.question6()))
                .question7(new HashSet<>(answers.question7()))
                .question8(new HashSet<>(answers.question8()))
                .question9(new HashSet<>(answers.question9()))
                .question10(new HashSet<>(answers.question10()))
                .build();
    }

    public void update(AccuracyRequest.answers answers) {
        updateCollection(this.question1, answers.question1());
        updateCollection(this.question2, answers.question2());
        updateCollection(this.question3, answers.question3());
        updateCollection(this.question4, answers.question4());
        updateCollection(this.question5, answers.question5());
        updateCollection(this.question6, answers.question6());
        updateCollection(this.question7, answers.question7());
        updateCollection(this.question8, answers.question8());
        updateCollection(this.question9, answers.question9());
        updateCollection(this.question10, answers.question10());
    }

    private void updateCollection(Set<String> target, java.util.List<String> source) {
        target.clear();
        if (source != null) {
            target.addAll(source);
        }
    }
}
