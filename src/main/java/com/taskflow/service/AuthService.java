package com.taskflow.service;

import com.taskflow.exception.ValidationException;
import com.taskflow.mapper.UserMapper;
import com.taskflow.model.dto.AuthResponse;
import com.taskflow.model.dto.LoginRequest;
import com.taskflow.model.dto.RegisterRequest;
import com.taskflow.model.entity.Role;
import com.taskflow.model.entity.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider,
                        UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(toUserDetails(saved));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toDTO(saved))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid credentials"));

        String token = jwtTokenProvider.generateToken(toUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toDTO(user))
                .build();
    }

    private UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), java.util.List.of());
    }
}
