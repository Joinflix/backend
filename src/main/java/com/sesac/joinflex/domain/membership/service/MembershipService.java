package com.sesac.joinflex.domain.membership.service;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.membership.repository.MembershipRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {
    private final MembershipRepository membershipRepository;

    public Membership findById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    }
}
