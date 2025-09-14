package com.project.zighang.domain.user.dto.request;

import com.project.zighang.domain.user.entity.Accuracy;

import java.util.ArrayList;
import java.util.List;

public record AccuracyRequest (
) {
    public record answers (
            List<String> question1,

            List<String> question2,

            List<String> question3,

            List<String> question4,

            List<String> question5,

            List<String> question6,

            List<String> question7,

            List<String> question8,

            List<String> question9,

            List<String> question10
    ) {
        public static AccuracyRequest.answers from(Accuracy accuracy) {
            return new AccuracyRequest.answers(
                    new ArrayList<>(accuracy.getQuestion1()),
                    new ArrayList<>(accuracy.getQuestion2()),
                    new ArrayList<>(accuracy.getQuestion3()),
                    new ArrayList<>(accuracy.getQuestion4()),
                    new ArrayList<>(accuracy.getQuestion5()),
                    new ArrayList<>(accuracy.getQuestion6()),
                    new ArrayList<>(accuracy.getQuestion7()),
                    new ArrayList<>(accuracy.getQuestion8()),
                    new ArrayList<>(accuracy.getQuestion9()),
                    new ArrayList<>(accuracy.getQuestion10())
            );
        }
    }
}
