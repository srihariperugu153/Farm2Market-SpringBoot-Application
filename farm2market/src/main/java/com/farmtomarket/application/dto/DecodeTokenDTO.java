package com.farmtomarket.application.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecodeTokenDTO {

    private String sub;
    private int iat;
    private int exp;
    private String role;
    private long id;
    private String email;
    private String mobile;


}
