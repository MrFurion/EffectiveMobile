package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

import static by.effective.mobile.eb.dto.constant.DtoConstant.AMOUNT_MUST_BE_POSITIVE;
import static by.effective.mobile.eb.dto.constant.DtoConstant.AMOUNT_SHOULD_NOT_BE_NULL;
import static by.effective.mobile.eb.dto.constant.DtoConstant.CARD_ID_SHOULD_NOT_BE_NULL;

@Data
public class RequestTransactionDto {

    @NotNull(message = CARD_ID_SHOULD_NOT_BE_NULL)
    private Long sourceCardId;
    @NotNull(message = CARD_ID_SHOULD_NOT_BE_NULL)
    private Long targetCardId;
    @NotNull(message = AMOUNT_SHOULD_NOT_BE_NULL)
    @Positive(message = AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal amount;
}
