package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLoginUserDto {
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be a valid email address")
    private String email;
    @Size(min = 2, max = 15, message = "Password should be between 2 and 15 character")
    private String password;
}
