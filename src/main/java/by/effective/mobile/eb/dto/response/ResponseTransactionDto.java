package by.effective.mobile.eb.dto.response;

import by.effective.mobile.eb.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResponseTransactionDto {

    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime transactionData;
}
