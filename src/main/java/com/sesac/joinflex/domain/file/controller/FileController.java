package com.sesac.joinflex.domain.file.controller;

import com.sesac.joinflex.domain.file.dto.response.FileUploadResponse;
import com.sesac.joinflex.domain.file.service.FileService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiPath.FILE)
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    //api/files/upload
    @PostMapping(value = ApiPath.UPLOADFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String url = fileService.uploadFile(file, userDetails.getId());
        return ResponseEntity.ok(FileUploadResponse.of(url));
    }

    //api/files
    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails){
        fileService.deleteFile(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}