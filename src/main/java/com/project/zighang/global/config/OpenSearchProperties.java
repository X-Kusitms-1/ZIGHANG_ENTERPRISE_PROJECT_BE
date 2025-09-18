package com.project.zighang.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "opensearch")
public class OpenSearchProperties {

    private String url = "http://localhost:9200";
    private String index = "ilhaeng-embeddings";
    private String vectorField = "jobReasoning";

    // Connection 설정 객체
    private Connection connection = new Connection();
    // Pool 설정 객체
    private Pool pool = new Pool();

    @Data
    public static class Connection {
        private int timeout = 5000;
        private int socketTimeout = 60000;
        private int maxRetryAttempts = 3;
        private int keepAliveTime = 30000;
        private int maxTotalConnections = 50;
        private int maxPerRoute = 10;
    }

    @Data
    public static class Pool {
        private int maxIdleTime = 30000;
        private int validateAfterInactivity = 2000;
    }
}