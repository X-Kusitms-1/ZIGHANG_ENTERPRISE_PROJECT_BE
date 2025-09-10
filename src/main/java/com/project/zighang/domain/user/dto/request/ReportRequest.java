package com.project.zighang.domain.user.dto.request;

public record ReportRequest (){
    public record Weekly(
            Integer year,
            Integer month,
            Integer weekOfMonth
    ) {}
}
