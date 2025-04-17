package by.effective.mobile.eb.services.constants;

public class Constants {
    private Constants() {
    }

    public static final String CARD_NOT_FOUND_WITH_ID = "card not found with id: {}";
    public static final String USER_NOT_HAVE_CARD_WITH_ID = "user not have card with id {}";
    public static final String LIMIT_NOT_SET_FOR_CARD_WITH_ID = "Limit not set for card with ID {}";
    public static final String CARD_UPDATED_WITH_ID = "card updated with id: {}";
    public static final String USER_NOT_FOUND_WITH_ID = "user not found with id {}";
    public static final String CARD_ALREADY_EXISTS_WITH_NUMBER_CARD = "card already exists with numberCard: {}";
    public static final String CARD_CREATED_SUCCESSFUL = "card created successful: {}";
    public static final String ACTIVE = "active";
    public static final String BLOCKED = "blocked";
    public static final String CARD_STATUS = "card status: {}";
    public static final String CARD_DELETED = "card deleted: {}";
    public static final String AMOUNT_TO_WITHDRAW_MUST_BE_POSITIVE_GOT = "Amount to withdraw must be positive, got {}";
    public static final String CARD_WITH_ID_IS_NOT_ACTIVE = "Card with ID {} is not active";
    public static final String TRANSACTION_EXCEEDS_DAILY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED = "Transaction exceeds daily limit for card with ID {}, spent: {}, limit: {}, requested: {}";
    public static final String TRANSACTION_EXCEEDS_MONTHLY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED = "Transaction exceeds monthly limit for card with ID {}, spent: {}, limit: {}, requested: {}";
    public static final String AMOUNT_TO_WITHDRAW_MUST_BE_POSITIVE = "Amount to withdraw must be positive";
    public static final String INSUFFICIENT_BALANCE_ON_CARD_WITH_ID_BALANCE_REQUIRED = "Insufficient balance on card with ID {}, balance: {}, required: {}";
}
