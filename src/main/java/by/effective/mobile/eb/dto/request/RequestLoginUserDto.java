package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static by.effective.mobile.eb.dto.constant.DtoConstant.EMAIL_SHOULD_BE_A_VALID_EMAIL_ADDRESS;
import static by.effective.mobile.eb.dto.constant.DtoConstant.EMAIL_SHOULD_NOT_BE_EMPTY;
import static by.effective.mobile.eb.dto.constant.DtoConstant.PASSWORD_SHOULD_BE_BETWEEN_2_AND_15_CHARACTER;

@Data
public class RequestLoginUserDto {

    @NotEmpty(message = EMAIL_SHOULD_NOT_BE_EMPTY)
    @Email(message = EMAIL_SHOULD_BE_A_VALID_EMAIL_ADDRESS)
    private String email;
    @Size(min = 2, max = 15, message = PASSWORD_SHOULD_BE_BETWEEN_2_AND_15_CHARACTER)
    private String password;
}
