package by.effective.mobile.eb.controller;


import by.effective.mobile.eb.dto.request.RequestLoginUserDto;
import by.effective.mobile.eb.dto.request.RequestRegisterUserDto;
import by.effective.mobile.eb.dto.response.ResponseRegisterUserDto;
import by.effective.mobile.eb.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user")
    @PostMapping("/signup")
    public ResponseEntity<ResponseRegisterUserDto> register(@Validated @RequestBody RequestRegisterUserDto registerUserDto) {
        ResponseRegisterUserDto registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Authenticate user by name and password")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody RequestLoginUserDto loginUserDto) {
        LoginResponse loginResponse = authenticationService.login(loginUserDto);
        return ResponseEntity.ok(loginResponse);
    }
}
