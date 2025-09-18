package com.project.zighang.global.client.opensearch;

import com.project.zighang.global.client.opensearch.dto.OpenSearchDto;

public interface PostRecommendsFinder {
    OpenSearchDto.SearchResponse<OpenSearchDto.JobReasoningSource> knnRaw(OpenSearchDto.KnnReq knnRequest);

    OpenSearchDto.KnnViewResponse knn(OpenSearchDto.KnnReq knnRequest);
}
