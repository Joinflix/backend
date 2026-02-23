package com.sesac.joinflix.global.common.controller;

import com.sesac.joinflix.global.common.constants.ApiPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.serverAddress}")
    private String serverAddress;

    @Value("${serverName}")
    private String serverName;

    @GetMapping(ApiPath.HEALTHCEHCK)
    public ResponseEntity<?> healthCheck() {
        Map<String, String> res = new TreeMap<>();
        res.put("serverName", serverName);
        res.put("serverAddress", serverAddress);
        res.put("serverPort", serverPort);
        res.put("env", env);
        return ResponseEntity.ok(res);
    }

    @GetMapping(ApiPath.ENV)
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}
