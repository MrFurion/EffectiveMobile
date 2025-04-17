package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import static by.effective.mobile.eb.dto.constant.DtoConstant.CARD_ID_SHOULD_NOT_BE_NULL;
import static by.effective.mobile.eb.dto.constant.DtoConstant.EXPIRY_DATE_SHOULD_NOT_BE_NULL;
import static by.effective.mobile.eb.dto.constant.DtoConstant.LIMIT_SHOULD_NOT_BE_NULL;
import static by.effective.mobile.eb.dto.constant.DtoConstant.NUMBER_CAR_SHOULD_BY_16_CHARACTER;

@Data
public class RequestCreatCardDto {

    @NotNull(message = EXPIRY_DATE_SHOULD_NOT_BE_NULL)
    private LocalDate expiryDate;

    @Size(min = 16, max = 16, message = NUMBER_CAR_SHOULD_BY_16_CHARACTER)
    private String numberCard;

    @NotNull(message = LIMIT_SHOULD_NOT_BE_NULL)
    private LimitDto limitDto;

    @NotNull(message = CARD_ID_SHOULD_NOT_BE_NULL)
    private Long userId;
}
