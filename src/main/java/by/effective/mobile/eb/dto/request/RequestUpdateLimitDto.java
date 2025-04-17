package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

import static by.effective.mobile.eb.dto.constant.DtoConstant.LIMIT_SHOULD_NOT_BE_NULL;

@Data
public class RequestUpdateLimitDto {

    @NotNull(message = LIMIT_SHOULD_NOT_BE_NULL)
    private BigDecimal dailyLimit;

    @NotNull(message = LIMIT_SHOULD_NOT_BE_NULL)
    private BigDecimal monthlyLimit;
}
