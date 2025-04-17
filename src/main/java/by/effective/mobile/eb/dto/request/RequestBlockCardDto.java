package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static by.effective.mobile.eb.dto.constant.DtoConstant.REASON_SHOULD_BETWEEN_1_AND_300_CHARACTER;
import static by.effective.mobile.eb.dto.constant.DtoConstant.REASON_SHOULD_NOT_BE_NULL;

@Data
public class RequestBlockCardDto {

    @NotEmpty(message = REASON_SHOULD_NOT_BE_NULL)
    @Size(min = 1, max = 300, message = REASON_SHOULD_BETWEEN_1_AND_300_CHARACTER)
    private String reason;
}
