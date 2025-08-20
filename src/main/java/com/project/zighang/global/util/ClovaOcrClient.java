package com.project.zighang.global.util;

import com.project.zighang.global.adapter.webapi.dto.ClovaOcrRequest;
import com.project.zighang.global.adapter.webapi.dto.ClovaOcrResponse;
import com.project.zighang.global.config.ClovaOcrClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "clovaOcrClient",
        url = "${clova.ocr.url}", // application.properties에 실제 URL 등록
        configuration = ClovaOcrClientConfig.class
)
public interface ClovaOcrClient {
    @PostMapping(value = "/general", consumes = MediaType.APPLICATION_JSON_VALUE)
    ClovaOcrResponse extractTextByUrl(@RequestBody ClovaOcrRequest request);
}
