package com.sesac.joinflix.global.common.controller;

import com.sesac.joinflix.global.common.constants.ApiPath;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    private String env;

    @GetMapping(ApiPath.HEALTHCEHCK)
    public ResponseEntity<?> healthCheck() {
        Map<String, String> res = new HashMap<>();

        return ResponseEntity.ok(res);
    }

    @GetMapping(ApiPath.ENV)
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}
