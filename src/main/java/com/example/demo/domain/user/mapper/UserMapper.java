package com.example.demo.domain.user.mapper;

import com.example.demo.domain.user.dto.MemberResponse;
import com.example.demo.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    MemberResponse entityToResponse(User user);
}