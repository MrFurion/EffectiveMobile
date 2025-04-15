package by.effective.mobile.eb.services.impl;

import by.effective.mobile.eb.controller.LoginResponse;
import by.effective.mobile.eb.dto.request.RequestLoginUserDto;
import by.effective.mobile.eb.dto.request.RequestRegisterUserDto;
import by.effective.mobile.eb.dto.response.ResponseRegisterUserDto;
import by.effective.mobile.eb.enums.Roles;
import by.effective.mobile.eb.models.User;
import by.effective.mobile.eb.repositories.UserRepository;
import by.effective.mobile.eb.services.AuthenticationService;
import by.effective.mobile.eb.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Transactional
    public ResponseRegisterUserDto signup(RequestRegisterUserDto input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(Roles.VIEWER.getRoleName());
        userRepository.save(user);

        ResponseRegisterUserDto registerUserDto = new ResponseRegisterUserDto();
        registerUserDto.setUsername(input.getUsername());

        return registerUserDto;
    }

    public LoginResponse login(RequestLoginUserDto input) {
        User authenticatedUser = authenticate(input);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return loginResponse;
    }

    private User authenticate(RequestLoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
