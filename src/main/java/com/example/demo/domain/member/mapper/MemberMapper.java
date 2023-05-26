package com.example.demo.domain.member.mapper;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.dto.MemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MemberMapper {
    @Mapping(source = "email", target = "email")
    MemberResponse entityToResponse(Member member);
}