package com.example.immovision.filters;

import com.example.immovision.dto.LoginRequest;
import com.example.immovision.entities.user.ACL;
import com.example.immovision.services.RefreshTokenService;
import com.example.immovision.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private RefreshTokenService refreshTokenService;



    public JsonAuthenticationFilter(ObjectMapper objectMapper, JwtUtil jwtUtil, UserDetailsService userDetailsService, RefreshTokenService refreshTokenService) {
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = refreshTokenService.createRefreshToken(username);

        UserDetails user = userDetailsService.loadUserByUsername(username);


        List<String> acls = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .distinct()
                .toList();

        List<String> roles = user.getAuthorities().stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .distinct()
                .toList();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "message", "Authentication successful",
                "token", accessToken,
                "refreshToken", refreshToken,
                "acls", acls,
                "role", roles.get(0),
                "emailUser", user.getUsername()
        ));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "message", "Authentication failed",
                "error", "Invalid username or password"
        ));
    }
}