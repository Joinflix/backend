package com.sesac.joinflix.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, Long userId);
    void deleteFile(Long userId);
}
