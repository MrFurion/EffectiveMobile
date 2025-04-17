package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

import static by.effective.mobile.eb.dto.constant.DtoConstant.AMOUNT_SHOULD_NOT_BE_NULL;
import static by.effective.mobile.eb.dto.constant.DtoConstant.CARD_ID_SHOULD_NOT_BE_NULL;

@Data
public class RequestWithdrawFromCard {

    @NotNull(message = CARD_ID_SHOULD_NOT_BE_NULL)
    private Long cardId;

    @NotNull(message = AMOUNT_SHOULD_NOT_BE_NULL)
    private BigDecimal amount;
}
