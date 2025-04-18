package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static by.effective.mobile.eb.dto.constant.DtoConstant.CARD_ID_SHOULD_NOT_BE_NULL;

@Data
public class RequestChangeStatusCardDto {

    @NotNull(message = CARD_ID_SHOULD_NOT_BE_NULL)
    private Long cardId;
}
