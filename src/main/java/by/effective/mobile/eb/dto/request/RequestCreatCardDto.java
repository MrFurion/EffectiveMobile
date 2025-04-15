package by.effective.mobile.eb.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestCreatCardDto {

    @NotNull(message = "Expiry date should not be null")
    private LocalDate expiryDate;

    @Size(min = 16, max = 16, message = "Number car should by 16 character")
    private String numberCard;

    @NotNull
    private LimitDto limitDto;
    @NotNull
    private Long userId;
}
