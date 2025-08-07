package com.codehows.shop.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter
public class MemberFormDto {
    private String name;
    private String email;
    private String password;
    private String address;

}

