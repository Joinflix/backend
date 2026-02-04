package com.sesac.joinflex.domain.payment.controller;

import com.sesac.joinflex.domain.payment.dto.request.PaymentCreateRequest;
import com.sesac.joinflex.domain.payment.dto.response.PaymentResponse;
import com.sesac.joinflex.domain.payment.service.PaymentService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    //api/payments/complete
    @PostMapping(ApiPath.PAYMENT_COMPLETE)
    public ResponseEntity<PaymentResponse> complete(
            @RequestBody PaymentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        String ip = NetworkUtil.getClientIp(servletRequest);
        String ua = NetworkUtil.getUserAgent(servletRequest);
        PaymentResponse response = paymentService.complete(request, userDetails.getId(), ip, ua);
        return ResponseEntity.ok(response);
    }

    //api/payments/me
    @GetMapping(ApiPath.PAYMENT_ME)
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PaymentResponse> responses = paymentService.getMyPaymentHistory(userDetails.getId());
        return ResponseEntity.ok(responses);
    }
}
