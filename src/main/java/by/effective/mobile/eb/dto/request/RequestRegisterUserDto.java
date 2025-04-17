package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static by.effective.mobile.eb.dto.constant.DtoConstant.EMAIL_SHOULD_NOT_BE_EMPTY;
import static by.effective.mobile.eb.dto.constant.DtoConstant.NAME_SHOULD_NOT_BE_EMPTY;
import static by.effective.mobile.eb.dto.constant.DtoConstant.PASSWORD_SHOULD_BE_BETWEEN_2_AND_15_CHARACTER;
import static by.effective.mobile.eb.dto.constant.DtoConstant.PASSWORD_SHOULD_NOT_BE_EMPTY;
import static by.effective.mobile.eb.dto.constant.DtoConstant.USERNAME_SHOULD_BE_BETWEEN_4_AND_30_CHARACTER;

@Data
public class RequestRegisterUserDto {

    @NotEmpty(message = NAME_SHOULD_NOT_BE_EMPTY)
    @Size(min = 4, max = 30, message = USERNAME_SHOULD_BE_BETWEEN_4_AND_30_CHARACTER)
    private String username;
    @NotEmpty(message = PASSWORD_SHOULD_NOT_BE_EMPTY)
    @Size(min = 2, max = 15, message = PASSWORD_SHOULD_BE_BETWEEN_2_AND_15_CHARACTER)
    private String password;
    @NotEmpty(message = EMAIL_SHOULD_NOT_BE_EMPTY)
    @Email
    private String email;
}
