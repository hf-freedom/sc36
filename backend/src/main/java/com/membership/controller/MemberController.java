package com.membership.controller;

import java.util.List;

import com.membership.dto.ApiResponse;
import com.membership.dto.CreateMemberRequest;
import com.membership.model.Member;
import com.membership.model.MemberLevel;
import com.membership.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public ApiResponse<Member> createMember(@RequestBody CreateMemberRequest request) {
        try {
            Member member = memberService.createMember(request.getName(), request.getPhone());
            return ApiResponse.success("会员创建成功", member);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Member> getMemberById(@PathVariable String id) {
        return memberService.getMemberById(id)
                .map(member -> ApiResponse.success(member))
                .orElse(ApiResponse.error("会员不存在"));
    }

    @GetMapping("/phone/{phone}")
    public ApiResponse<Member> getMemberByPhone(@PathVariable String phone) {
        return memberService.getMemberByPhone(phone)
                .map(member -> ApiResponse.success(member))
                .orElse(ApiResponse.error("会员不存在"));
    }

    @GetMapping
    public ApiResponse<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ApiResponse.success(members);
    }

    @PutMapping("/{id}/level/{level}")
    public ApiResponse<Member> updateMemberLevel(
            @PathVariable String id,
            @PathVariable MemberLevel level) {
        try {
            Member member = memberService.updateMemberLevel(id, level);
            return ApiResponse.success("会员等级更新成功", member);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
