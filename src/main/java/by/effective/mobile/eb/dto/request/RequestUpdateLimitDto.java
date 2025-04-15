package by.effective.mobile.eb.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestUpdateLimitDto {
    private BigDecimal dailyLimit;

    private BigDecimal monthlyLimit;
}
