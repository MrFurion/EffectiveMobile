package by.effective.mobile.eb.dto.response;

import by.effective.mobile.eb.dto.request.LimitDto;
import by.effective.mobile.eb.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseFoundCardDto {

    private String numberCard;
    private LocalDate expiryDate;
    private CardStatus cardStatus;
    private BigDecimal balance;
    private List<ResponseTransactionDto> transactions = new ArrayList<>();
    private LimitDto limit;
}
