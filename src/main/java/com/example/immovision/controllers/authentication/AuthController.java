package com.example.immovision.controllers.authentication;

import com.example.immovision.dto.*;
import com.example.immovision.entities.user.ACL;
import com.example.immovision.entities.user.User;
import com.example.immovision.mappers.user.UserMapper;
import com.example.immovision.services.RefreshTokenService;
import com.example.immovision.services.UserService;
import com.example.immovision.services.customs.CustomUserDetailsService;
import com.example.immovision.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserMapper userMapper;



    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        try {

            boolean isChanged = userService.changePassword(
                    changePasswordRequest.getEmail(),
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword()
            );

            if (isChanged) {
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Current password is incorrect"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Password change failed", "error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public void refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest,
                             HttpServletResponse response) throws IOException {
        try {

            String newAccessToken = refreshTokenService.refreshAccessToken(
                    refreshTokenRequest.getRefreshToken()
            );
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Token refreshed successfully",
                    "token", newAccessToken
            ));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Token refresh failed",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenRequest refreshTokenRequest,
                       HttpServletResponse response) throws IOException {
        try {

            refreshTokenService.revokeRefreshToken(refreshTokenRequest.getRefreshToken());

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Logout successful"
            ));
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Logout failed",
                    "error", e.getMessage()
            ));
        }
    }



    @PostMapping("/login")
    public AuthenticationResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        Optional<User> user = userService.findByEmail(userDetails.getUsername());
        if(user.isPresent()) {
            User user2 = user.get();
            List<String> acls = user2.getRoles().getAcls().stream()
                    .map(ACL::getName)
                    .distinct()
                    .collect(Collectors.toList());

            return new AuthenticationResponse(jwt, acls);
        }

        AuthenticationResponse response = new AuthenticationResponse(jwt, null);
        System.out.println("Response: " + response);
        return response;
    }

    @PostMapping("/google-signin")
    public AuthenticationResponse googleSignIn(@RequestBody GoogleTokenDto tokenDto) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(NET_HTTP_TRANSPORT, JSON_FACTORY)
                    .setAudience(Collections.singletonList("391094031907-taklbls70hk48us3rolpipepiprfdjkl.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenDto.getIdToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String googleId = payload.getSubject();
                String name = (String) payload.get("name");

                User user = userService.findOrCreateUserByGoogle(email, googleId, name);
                String jwt = jwtUtil.generateToken(user.getEmail());

                List<String> acls = user.getRoles().getAcls().stream()
                        .map(ACL::getName)
                        .distinct()
                        .collect(Collectors.toList());

                return new AuthenticationResponse(jwt, acls);
            }

            return new AuthenticationResponse(null, null);
        } catch (Exception e) {
            return new AuthenticationResponse(null, null);
        }
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new JacksonFactory()
        )
                .setAudience(Collections.singletonList("391094031907-taklbls70hk48us3rolpipepiprfdjkl.apps.googleusercontent.com"))
                .build();

        GoogleIdToken token = verifier.verify(idToken);
        return token != null ? token.getPayload() : null;
    }






}
