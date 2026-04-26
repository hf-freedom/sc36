package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.membership.model.Member;
import com.membership.model.MemberLevel;
import com.membership.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member createMember(String name, String phone) {
        if (memberRepository.findByPhone(phone).isPresent()) {
            throw new RuntimeException("会员手机号已存在");
        }
        
        Member member = new Member();
        member.setId(UUID.randomUUID().toString());
        member.setName(name);
        member.setPhone(phone);
        member.setLevel(MemberLevel.NORMAL);
        member.setBalance(BigDecimal.ZERO);
        member.setGiftedBalance(BigDecimal.ZERO);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        
        return memberRepository.save(member);
    }

    public Optional<Member> getMemberById(String id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member updateMemberLevel(String memberId, MemberLevel level) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));
        member.setLevel(level);
        member.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public void updateBalance(String memberId, BigDecimal principalChange, BigDecimal giftChange) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));
        member.setBalance(member.getBalance().add(principalChange));
        member.setGiftedBalance(member.getGiftedBalance().add(giftChange));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    public MemberLevel getMemberLevel(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));
        return member.getLevel();
    }
}
