package com.sesac.joinflex.domain.file.controller;

import com.sesac.joinflex.domain.file.dto.response.FileUploadResponse;
import com.sesac.joinflex.domain.file.service.FileService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiPath.FILE)
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = ApiPath.UPLOADFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        return ResponseEntity.ok(FileUploadResponse.of(url));
    }
}