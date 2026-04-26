package com.membership.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.membership.model.Member;

import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    private final Map<String, Member> members = new ConcurrentHashMap<>();

    public Member save(Member member) {
        members.put(member.getId(), member);
        return member;
    }

    public Optional<Member> findById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    public Optional<Member> findByPhone(String phone) {
        return members.values().stream()
                .filter(m -> phone.equals(m.getPhone()))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }

    public void deleteById(String id) {
        members.remove(id);
    }

    public boolean existsById(String id) {
        return members.containsKey(id);
    }

    public long count() {
        return members.size();
    }
}
