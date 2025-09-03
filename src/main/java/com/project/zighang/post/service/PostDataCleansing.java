package com.project.zighang.post.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PostDataCleansing {
    public static List<String> cleanDepthTwo(String data) {
        if (data == null || data.trim().isEmpty()) {
            return List.of();
        }
        String[] splitData = data.split(",");
        return Arrays.stream(splitData)
                .map(s -> s.trim().replace("'", ""))
                .collect(Collectors.toList());
    }
}
