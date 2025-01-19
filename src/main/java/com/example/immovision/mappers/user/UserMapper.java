package com.example.immovision.mappers.user;

import com.example.immovision.dto.UserInfoDTO;
import com.example.immovision.entities.user.User;


public interface UserMapper {

    UserInfoDTO userToUserInfoDTO(User user);
}
