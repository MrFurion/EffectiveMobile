package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestChangeStatusCardDto {

    @NotNull
    private Long cardId;
}
