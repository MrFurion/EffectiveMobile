package by.effective.mobile.eb.services.impl;

import by.effective.mobile.eb.dto.request.RequestCreatCardDto;
import by.effective.mobile.eb.dto.request.RequestUpdateLimitDto;
import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.enums.CardStatus;
import by.effective.mobile.eb.exception.CardAlreadyExistsException;
import by.effective.mobile.eb.exception.CardNotFoundException;
import by.effective.mobile.eb.exception.UserNotFoundException;
import by.effective.mobile.eb.mapper.CardMapper;
import by.effective.mobile.eb.mapper.LimitMapper;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Limit;
import by.effective.mobile.eb.models.User;
import by.effective.mobile.eb.repositories.CardRepository;
import by.effective.mobile.eb.repositories.UserRepository;
import by.effective.mobile.eb.services.CardService;
import by.effective.mobile.eb.util.EncryptionNumberCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static by.effective.mobile.eb.services.constants.Constants.ACTIVE;
import static by.effective.mobile.eb.services.constants.Constants.BLOCKED;
import static by.effective.mobile.eb.services.constants.Constants.CARD_ALREADY_EXISTS_WITH_NUMBER_CARD;
import static by.effective.mobile.eb.services.constants.Constants.CARD_CREATED_SUCCESSFUL;
import static by.effective.mobile.eb.services.constants.Constants.CARD_DELETED;
import static by.effective.mobile.eb.services.constants.Constants.CARD_NOT_FOUND_WITH_ID;
import static by.effective.mobile.eb.services.constants.Constants.CARD_STATUS;
import static by.effective.mobile.eb.services.constants.Constants.CARD_UPDATED_WITH_ID;
import static by.effective.mobile.eb.services.constants.Constants.USER_NOT_FOUND_WITH_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public ResponseFoundCardDto findById(Long cardId) {
        return CardMapper.INSTANCE.toResponseFoundCardDto(cardRepository.findById(cardId).orElseThrow(CardNotFoundException::new));
    }

    public Page<ResponseFoundCardDto> findAllCards(Pageable pageable) {
        Page<Card> cardPage = cardRepository.findAll(pageable);
        return cardPage.map(CardMapper.INSTANCE::toResponseFoundCardDto);
    }

    @Transactional
    public ResponseFoundCardDto changeLimit(Long cardId, RequestUpdateLimitDto requestUpdateLimitDto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.error(CARD_NOT_FOUND_WITH_ID, cardId);
            return new CardNotFoundException();
        });
        Limit limit = card.getLimit();

        Optional.ofNullable(requestUpdateLimitDto.getDailyLimit()).ifPresent(limit::setDailyLimit);
        Optional.ofNullable(requestUpdateLimitDto.getMonthlyLimit()).ifPresent(limit::setMonthlyLimit);

        Card cardUpdateLimit = cardRepository.save(card);
        log.info(CARD_UPDATED_WITH_ID, cardId);
        return CardMapper.INSTANCE.toResponseFoundCardDto(cardUpdateLimit);
    }

    @Override
    @Transactional
    public ResponseCardDto createCard(RequestCreatCardDto requestCardDto) throws Exception {
        User user = userRepository.findById(requestCardDto.getUserId()).orElseThrow(() -> {
            log.error(USER_NOT_FOUND_WITH_ID, requestCardDto.getUserId());
            return new UserNotFoundException();
        });

        String encryptedNumberCard = EncryptionNumberCard.encrypt(requestCardDto.getNumberCard());

        if (cardRepository.existsByNumberCard(encryptedNumberCard)) {
            log.error(CARD_ALREADY_EXISTS_WITH_NUMBER_CARD, encryptedNumberCard);
            throw new CardAlreadyExistsException();
        }
        Card card = CardMapper.INSTANCE.toCard(requestCardDto);
        card.setCardStatus(CardStatus.NO_STATUS);
        card.setNumberCard(EncryptionNumberCard.encrypt(requestCardDto.getNumberCard()));
        card.setExpiryDate(requestCardDto.getExpiryDate());
        card.setBalance(BigDecimal.ZERO);
        Limit limit = LimitMapper.INSTANCE.toLimit(requestCardDto.getLimitDto());
        limit.setCard(card);
        card.setLimit(limit);
        card.setUser(user);
        cardRepository.save(card);
        log.info(CARD_CREATED_SUCCESSFUL, card.getNumberCard());
        return CardMapper.INSTANCE.toResponseCardDto(card);
    }

    @Override
    @Transactional
    public ResponseCardDto changeStatusCard(Long cardId, String status) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.error(CARD_NOT_FOUND_WITH_ID, cardId);
            return new CardNotFoundException();
        });

        switch (status.toLowerCase()) {
            case ACTIVE -> card.setCardStatus(CardStatus.ACTIVE);
            case BLOCKED -> card.setCardStatus(CardStatus.BLOCKED);
            default -> card.setCardStatus(CardStatus.NO_STATUS);
        }
        log.info(CARD_STATUS, card.getCardStatus());
        return CardMapper.INSTANCE.toResponseCardDto(cardRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        int deleteCount = cardRepository.deleteCardByIdCard(cardId);
        if (deleteCount == 0) {
            log.error(CARD_NOT_FOUND_WITH_ID, cardId);
            throw new CardNotFoundException();
        }
        log.info(CARD_DELETED, cardId);
    }
}
