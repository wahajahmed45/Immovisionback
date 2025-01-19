package com.example.immovision.services.customs;
import com.example.immovision.entities.user.Role;
import com.example.immovision.repositories.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;



    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.immovision.entities.user.User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User  not found with email: " + email));

        Role role = userEntity.getRoles();

        String roleName = role != null ? role.getName() : null;


        List<String> acls = role != null ? role.getAcls().stream()
                .map(acl -> acl.getName().toUpperCase())
                .distinct()
                .toList() : List.of();


        List<String> authorities = new ArrayList<>();
        if (roleName != null) {
            authorities.add("ROLE_" + roleName);
        }
        authorities.addAll(acls);

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(roleName != null ? new String[]{roleName} : new String[0])
                .authorities(authorities.toArray(new String[0]))
                .build();
    }
}
