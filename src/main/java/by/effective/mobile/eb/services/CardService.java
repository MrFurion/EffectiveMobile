package by.effective.mobile.eb.services;

import by.effective.mobile.eb.dto.request.RequestCreatCardDto;
import by.effective.mobile.eb.dto.request.RequestUpdateLimitDto;
import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    /**
     * Finds a card by its ID and returns its details.
     *
     * @param cardId the ID of the card
     * @return ResponseFoundCardDto with card details
     */
    ResponseFoundCardDto findById(Long cardId);

    /**
     * Retrieves a paginated list of all cards with their details.
     *
     * @param pageable pagination information
     * @return Page<ResponseFoundCardDto> with cards
     */
    Page<ResponseFoundCardDto> findAllCards(Pageable pageable);

    /**
     * Updates the daily and monthly limits of a card by its ID.
     *
     * @param cardId the ID of the card
     * @param requestUpdateLimitDto DTO with new limit values
     * @return ResponseFoundCardDto with updated card details
     */
    ResponseFoundCardDto changeLimit(Long cardId, RequestUpdateLimitDto requestUpdateLimitDto);

    /**
     * Creates a new card based on the provided data.
     *
     * @param cardDto DTO with card creation data
     * @return ResponseCardDto with created card details
     */
    ResponseCardDto createCard(RequestCreatCardDto cardDto) throws Exception;

    /**
     * Changes the status of a card (e.g., "active" or "block") by its ID.
     *
     * @param cardId the ID of the card
     * @param status the new status ("active" or "block")
     * @return ResponseCardDto with updated card details
     */
    ResponseCardDto changeStatusCard(Long cardId, String status);

    /**
     * Deletes a card by its ID.
     *
     * @param cardId the ID of the card
     */
    void deleteCard(Long cardId);
}
