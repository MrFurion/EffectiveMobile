package by.effective.mobile.eb.data;

import by.effective.mobile.eb.dto.request.LimitDto;
import by.effective.mobile.eb.dto.request.RequestCreatCardDto;
import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.enums.CardStatus;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Limit;
import by.effective.mobile.eb.models.User;
import by.effective.mobile.eb.util.EncryptionNumberCard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;


public class TestData {
    public static final BigDecimal NEW_DAILY_LIMIT = new BigDecimal("5000.00");
    public static final BigDecimal NEW_MONTHLY_LIMIT = new BigDecimal("20000.00");
    public static final Long USER_ID = 2L;
    public static final Long CARD_ID = 1L;
    public static final String PLAIN_CARD_NUMBER = "1234-5678-9012-3456";
    private static final String ENCRYPTED_CARD_NUMBER;

    static {
        try {
            ENCRYPTED_CARD_NUMBER = EncryptionNumberCard.encrypt(PLAIN_CARD_NUMBER);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt card number for test", e);
        }
    }

    public static final BigDecimal INITIAL_DAILY_LIMIT = new BigDecimal("1000.00");
    public static final BigDecimal INITIAL_MONTHLY_LIMIT = new BigDecimal("5000.00");
    public static final LocalDate EXPIRY_DATE = LocalDate.of(2025, 12, 31);
    public static final BigDecimal BALANCE = new BigDecimal("1000.00");

    public static Card createTestCard() {
        Limit limit = new Limit();
        limit.setDailyLimit(INITIAL_DAILY_LIMIT);
        limit.setMonthlyLimit(INITIAL_MONTHLY_LIMIT);

        User user = createTestUser();

        Card card = Card.builder()
                .id(CARD_ID)
                .numberCard(ENCRYPTED_CARD_NUMBER)
                .user(user)
                .expiryDate(EXPIRY_DATE)
                .cardStatus(CardStatus.NO_STATUS)
                .balance(BALANCE)
                .transactions(new ArrayList<>())
                .limit(limit)
                .blockReasons(new HashSet<>())
                .build();

        limit.setCard(card);
        return card;
    }

    public static ResponseFoundCardDto createTestResponseFoundCardDto() {
        ResponseFoundCardDto dto = new ResponseFoundCardDto();
        dto.setNumberCard(PLAIN_CARD_NUMBER);
        dto.setExpiryDate(EXPIRY_DATE);
        dto.setCardStatus(CardStatus.NO_STATUS);
        dto.setBalance(BALANCE);
        dto.setTransactions(new ArrayList<>());

        LimitDto limitDto = new LimitDto();
        limitDto.setDailyLimit(INITIAL_DAILY_LIMIT);
        limitDto.setMonthlyLimit(INITIAL_MONTHLY_LIMIT);
        dto.setLimit(limitDto);

        return dto;
    }

    public static User createTestUser() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }

    public static RequestCreatCardDto createTestRequestCreatCardDto() {
        RequestCreatCardDto dto = new RequestCreatCardDto();
        dto.setUserId(USER_ID);
        dto.setNumberCard(PLAIN_CARD_NUMBER);
        dto.setExpiryDate(EXPIRY_DATE);

        LimitDto limitDto = new LimitDto();
        limitDto.setDailyLimit(INITIAL_DAILY_LIMIT);
        limitDto.setMonthlyLimit(INITIAL_MONTHLY_LIMIT);
        dto.setLimitDto(limitDto);

        return dto;
    }

    public static ResponseCardDto createTestResponseCardDto() {
        ResponseCardDto dto = new ResponseCardDto();
        dto.setId(CARD_ID);
        dto.setCardStatus(CardStatus.NO_STATUS);
        return dto;
    }
}
