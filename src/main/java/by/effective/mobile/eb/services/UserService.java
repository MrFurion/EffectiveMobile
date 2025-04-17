package by.effective.mobile.eb.services;

import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.request.RequestWithdrawFromCard;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Retrieves a list of cards belonging to the current user.
     *
     * @return a list of {@link ResponseFoundCardDto} containing the user's cards
     */
    List<ResponseFoundCardDto> findCardsOfUser();

    /**
     * Blocks a card based on the provided request and card ID.
     *
     * @param requestBlockCardDto the request containing details for blocking the card
     * @param cardId the ID of the card to be blocked
     */
    void blockedCard(RequestBlockCardDto requestBlockCardDto, Long cardId);

    /**
     * Transfers funds between two cards based on the provided transaction details.
     *
     * @param requestTransactionDto the request containing source card ID, target card ID, and amount
     */
    void transferBetweenCards(RequestTransactionDto requestTransactionDto);

    /**
     * Adds the specified amount to the balance of the card with the given ID.
     *
     * @param idCard the ID of the card to add balance to
     * @param amount the amount to add to the card's balance
     */
    void addBalance(Long idCard, BigDecimal amount);

    /**
     * Withdraws the specified amount from the card based on the provided request.
     *
     * @param requestWithdrawFromCard the request containing the card ID and amount to withdraw
     */
    void withdrawFromCard(RequestWithdrawFromCard requestWithdrawFromCard);
}
