package com.example.demo.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 JPA를 사용하여 데이터베이스에 저장되는 권한(Authority) 엔티티 클래스
 */

@Entity
@Table(name = "authority")
@Data
@NoArgsConstructor
public class authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    public static authority of(String authorityName) {
        authority authority = new authority();
        authority.authorityName = authorityName;
        return authority;
    }
}