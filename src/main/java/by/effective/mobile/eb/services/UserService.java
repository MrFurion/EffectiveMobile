package by.effective.mobile.eb.services;

import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    List<ResponseFoundCardDto> findCardsOfUser();
    void blockedCard(RequestBlockCardDto requestBlockCardDto, Long cardId);
    void transferBetweenCards(RequestTransactionDto requestTransactionDto);
    void addBalance(Long idCard, BigDecimal amount);
}
