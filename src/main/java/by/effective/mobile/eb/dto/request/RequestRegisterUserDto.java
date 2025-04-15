package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestRegisterUserDto {
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 4, max = 30, message = "Username should be between 4 and 30 character")
    private String username;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 2, max = 15, message = "Password should be between 2 and 15 character")
    private String password;
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
}
