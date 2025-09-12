package com.project.zighang.domain.user.dto.request;

import com.project.zighang.domain.user.entity.Accuracy;

public record AccuracyRequest (
) {
    public record answers (
            String question1,

            String question2,

            String question3,

            String question4,

            String question5,

            String question6,

            String question7,

            String question8,

            String question9,

            String question10
    ) {
        public static AccuracyRequest.answers from(Accuracy accuracy) {
            return new AccuracyRequest.answers(
                    accuracy.getQuestion1(),
                    accuracy.getQuestion2(),
                    accuracy.getQuestion3(),
                    accuracy.getQuestion4(),
                    accuracy.getQuestion5(),
                    accuracy.getQuestion6(),
                    accuracy.getQuestion7(),
                    accuracy.getQuestion8(),
                    accuracy.getQuestion9(),
                    accuracy.getQuestion10()
            );
        }
    }
}
