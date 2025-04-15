package by.effective.mobile.eb.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestTransactionDto {
    private Long sourceCardId;
    private Long targetCardId;
    private BigDecimal amount;
}
