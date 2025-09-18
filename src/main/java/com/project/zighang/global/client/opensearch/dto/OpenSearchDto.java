package com.project.zighang.global.client.opensearch.dto;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class OpenSearchDto {

    public static record KnnReq(List<Double> vector, Integer k) {
        public KnnReq {
            Objects.requireNonNull(vector, "vector is null");
            if (k == null || k <= 0) k = 5;
        }
    }

    public static record SearchResponse<T>(int took, boolean timed_out, Shards _shards, Hits<T> hits) {
        public <R> List<R> mapHits(Function<Hit<T>, R> mapper) {
            return hits != null && hits.hits() != null
                    ? hits.hits().stream().map(mapper).toList()
                    : List.of();
        }
    }

    public static record KnnViewResponse(int took, int total, List<KnnView> items) {
        public static KnnViewResponse from(SearchResponse<JobReasoningSource> response) {
            var items = response.mapHits(h -> new KnnView(
                    h._id(),
                    h._score() != null ? h._score() : 0.0,
                    h._source() != null ? h._source().doc_id() : null
            ));
            int total = (response.hits() != null && response.hits().total() != null) ? response.hits().total().value() : items.size();
            return new KnnViewResponse(response.took(), total, items);
        }
    }

    public static record Shards(int total, int successful, int skipped, int failed) {}
    public static record Hits<T>(Total total, Double max_score, List<Hit<T>> hits) {}
    public static record Total(int value, String relation) {}
    public static record Hit<T>(String _index, String _id, Double _score, T _source) {}
    public static record JobReasoningSource(String doc_id, List<Double> jobReasoning) {}
    public static record KnnView(String id, double score, String doc_id) {}

}