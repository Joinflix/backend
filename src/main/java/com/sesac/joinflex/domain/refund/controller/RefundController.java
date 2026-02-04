package com.sesac.joinflex.domain.refund.controller;

import com.sesac.joinflex.domain.refund.dto.request.RefundRequest;
import com.sesac.joinflex.domain.refund.dto.response.RefundResponse;
import com.sesac.joinflex.domain.refund.service.RefundService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.REFUND)
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    //api/refunds
    @PostMapping
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody RefundRequest request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                                       HttpServletRequest httpServletRequest) {
        String ip = NetworkUtil.getClientIp(httpServletRequest);
        String ua = NetworkUtil.getUserAgent(httpServletRequest);
        RefundResponse response = refundService.createRefund(request, userDetails.getId(), ip, ua);
        return ResponseEntity.ok(response);
    }
}
