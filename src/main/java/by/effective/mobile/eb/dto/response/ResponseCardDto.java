package by.effective.mobile.eb.dto.response;

import by.effective.mobile.eb.enums.CardStatus;
import lombok.Data;

@Data
public class ResponseCardDto {
    private Long id;
    private CardStatus cardStatus;
}
