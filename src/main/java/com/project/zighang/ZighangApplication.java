package com.project.zighang;

import com.project.zighang.global.config.OpenSearchProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(OpenSearchProperties.class)
public class ZighangApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZighangApplication.class, args);
	}

}
