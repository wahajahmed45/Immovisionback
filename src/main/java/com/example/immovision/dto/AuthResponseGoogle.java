package com.example.immovision.dto;

import com.example.immovision.entities.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseGoogle {
    private String token;
    private UserInfoDTO user;

    private List<String> acls;

    private String role;
}
