package com.example.immovision.mappers.user;

import com.example.immovision.dto.UserInfoDTO;
import com.example.immovision.entities.user.Role;
import com.example.immovision.entities.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

        @Override
        public UserInfoDTO userToUserInfoDTO(User user) {
            if ( user == null ) {
                return null;
            }

            UserInfoDTO userInfoDTO = new UserInfoDTO();

            userInfoDTO.setEmail( user.getEmail() );
            userInfoDTO.setRole( userRolesName( user ) );
            userInfoDTO.setName( user.getName() );

            return userInfoDTO;
        }

        private String userRolesName(User user) {
            if ( user == null ) {
                return null;
            }
            Role roles = user.getRoles();
            if ( roles == null ) {
                return null;
            }
            String name = roles.getName();
            if ( name == null ) {
                return null;
            }
            return name;
        }
}
