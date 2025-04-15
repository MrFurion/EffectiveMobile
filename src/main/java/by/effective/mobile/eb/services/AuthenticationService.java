package by.effective.mobile.eb.services;

import by.effective.mobile.eb.controller.LoginResponse;
import by.effective.mobile.eb.dto.request.RequestLoginUserDto;
import by.effective.mobile.eb.dto.request.RequestRegisterUserDto;
import by.effective.mobile.eb.dto.response.ResponseRegisterUserDto;

public interface AuthenticationService {
    /**
     * Registers a new user in the system.
     *
     * @param input the registration details of the user
     * @return the registered user
     */
    ResponseRegisterUserDto signup(RequestRegisterUserDto input);

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param input DTO with login credentials (username, password)
     * @return {@link LoginResponse} with JWT token and expiration time
     */
    LoginResponse login(RequestLoginUserDto input);
}
