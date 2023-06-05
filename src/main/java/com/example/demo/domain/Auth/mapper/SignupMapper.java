package com.example.demo.domain.Auth.mapper;

import com.example.demo.domain.Auth.dto.SignupRequest;
import com.example.demo.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SignupMapper {

    SignupMapper INSTANCE = Mappers.getMapper(SignupMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "uri", target = "uri")
    @Mapping(target = "authorities", expression = "java(new java.util.HashSet<>(java.util.Collections.singletonList(com.example.demo.domain.user.entity.authority.of(\"ROLE_USER\"))))")
    @Mapping(target = "activated", constant = "true")
    User toEntity(SignupRequest signupRequest);
}