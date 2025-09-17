package com.project.zighang.global.client.objectStorage.service;

import java.util.List;

public interface PdfToImageConverter {
    List<String> convertPdfToImages(String pdfUrl);
}
