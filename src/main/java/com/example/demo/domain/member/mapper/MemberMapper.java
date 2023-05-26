package com.example.demo.domain.member.mapper;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.dto.MemberResponse;
import org.mapstruct.Mapper;

@Mapper
public interface MemberMapper {
    MemberResponse entityToResponse(Member member);
}