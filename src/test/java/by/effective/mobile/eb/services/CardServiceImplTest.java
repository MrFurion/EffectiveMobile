package by.effective.mobile.eb.services;

import by.effective.mobile.eb.data.TestData;
import by.effective.mobile.eb.dto.request.RequestCreatCardDto;
import by.effective.mobile.eb.dto.request.RequestUpdateLimitDto;
import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.enums.CardStatus;
import by.effective.mobile.eb.exception.CardAlreadyExistsException;
import by.effective.mobile.eb.exception.CardNotFoundException;
import by.effective.mobile.eb.exception.UserNotFoundException;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Limit;
import by.effective.mobile.eb.models.User;
import by.effective.mobile.eb.repositories.CardRepository;
import by.effective.mobile.eb.repositories.UserRepository;
import by.effective.mobile.eb.services.impl.CardServiceImpl;
import by.effective.mobile.eb.util.EncryptionNumberCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static by.effective.mobile.eb.data.TestData.CARD_ID;
import static by.effective.mobile.eb.data.TestData.NEW_DAILY_LIMIT;
import static by.effective.mobile.eb.data.TestData.NEW_MONTHLY_LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardServiceImpl;

    @Test
    void findByIdWhenCardExists() {
        // Given
        Card card = TestData.createTestCard();
        ResponseFoundCardDto expectedDto = TestData.createTestResponseFoundCardDto();

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));

        // When
        ResponseFoundCardDto actualDto = cardServiceImpl.findById(TestData.CARD_ID);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto.getNumberCard(), actualDto.getNumberCard());
        assertEquals(expectedDto.getExpiryDate(), actualDto.getExpiryDate());
        assertEquals(expectedDto.getCardStatus(), actualDto.getCardStatus());
        assertEquals(expectedDto.getBalance(), actualDto.getBalance());
        assertEquals(expectedDto.getTransactions(), actualDto.getTransactions());
        assertEquals(expectedDto.getLimit(), actualDto.getLimit());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
    }

    @Test
    void findByIdWhenCardNotFound() {
        // Given
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardServiceImpl.findById(CARD_ID));
        verify(cardRepository, times(1)).findById(CARD_ID);
    }

    @Test
    void findAllCardsWhenCardsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Card card = TestData.createTestCard();
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageable, 1);
        ResponseFoundCardDto expectedDto = TestData.createTestResponseFoundCardDto();

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        // When
        Page<ResponseFoundCardDto> result = cardServiceImpl.findAllCards(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        ResponseFoundCardDto actualDto = result.getContent().get(0);
        assertEquals(expectedDto.getNumberCard(), actualDto.getNumberCard());
        assertEquals(expectedDto.getExpiryDate(), actualDto.getExpiryDate());
        assertEquals(expectedDto.getCardStatus(), actualDto.getCardStatus());
        assertEquals(expectedDto.getBalance(), actualDto.getBalance());
        assertEquals(expectedDto.getTransactions(), actualDto.getTransactions());
        assertEquals(expectedDto.getLimit(), actualDto.getLimit());
        verify(cardRepository, times(1)).findAll(pageable);
    }

    @Test
    void findAllCardsWhenNoCardsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Card> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(cardRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<ResponseFoundCardDto> result = cardServiceImpl.findAllCards(pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(cardRepository, times(1)).findAll(pageable);
    }

    @Test
    void changeLimitWhenCardExistsAndBothLimitsUpdated() {
        // Given
        Card card = TestData.createTestCard();
        Limit limit = card.getLimit();
        BigDecimal originalDailyLimit = limit.getDailyLimit();
        BigDecimal originalMonthlyLimit = limit.getMonthlyLimit();

        RequestUpdateLimitDto request = new RequestUpdateLimitDto();
        request.setDailyLimit(NEW_DAILY_LIMIT);
        request.setMonthlyLimit(NEW_MONTHLY_LIMIT);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        ResponseFoundCardDto actualDto = cardServiceImpl.changeLimit(TestData.CARD_ID, request);

        // Then
        assertNotNull(actualDto);
        assertEquals(TestData.PLAIN_CARD_NUMBER, actualDto.getNumberCard());
        assertEquals(TestData.EXPIRY_DATE, actualDto.getExpiryDate());
        assertEquals(TestData.BALANCE, actualDto.getBalance());
        assertEquals(NEW_DAILY_LIMIT, limit.getDailyLimit());
        assertEquals(NEW_MONTHLY_LIMIT, limit.getMonthlyLimit());
        assertNotEquals(originalDailyLimit, limit.getDailyLimit());
        assertNotEquals(originalMonthlyLimit, limit.getMonthlyLimit());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changeLimitWhenCardExistsAndOnlyDailyLimitUpdated() {
        // Given
        Card card = TestData.createTestCard();
        Limit limit = card.getLimit();
        BigDecimal originalDailyLimit = limit.getDailyLimit();
        BigDecimal originalMonthlyLimit = limit.getMonthlyLimit();

        RequestUpdateLimitDto request = new RequestUpdateLimitDto();
        request.setDailyLimit(NEW_DAILY_LIMIT);
        request.setMonthlyLimit(null);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        ResponseFoundCardDto actualDto = cardServiceImpl.changeLimit(TestData.CARD_ID, request);

        // Then
        assertNotNull(actualDto);
        assertEquals(TestData.PLAIN_CARD_NUMBER, actualDto.getNumberCard());
        assertEquals(TestData.EXPIRY_DATE, actualDto.getExpiryDate());
        assertEquals(TestData.BALANCE, actualDto.getBalance());
        assertEquals(NEW_DAILY_LIMIT, limit.getDailyLimit());
        assertEquals(originalMonthlyLimit, limit.getMonthlyLimit());
        assertNotEquals(originalDailyLimit, limit.getDailyLimit());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changeLimitWhenCardNotFound() {
        // Given
        RequestUpdateLimitDto request = new RequestUpdateLimitDto();
        request.setDailyLimit(NEW_DAILY_LIMIT);
        request.setMonthlyLimit(NEW_MONTHLY_LIMIT);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardServiceImpl.changeLimit(TestData.CARD_ID, request));
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
    }

    @Test
    void createCardWhenUserExistsAndCardDoesNotExist() throws Exception {
        // Given
        RequestCreatCardDto request = TestData.createTestRequestCreatCardDto();
        User user = TestData.createTestUser();
        ResponseCardDto expectedDto = TestData.createTestResponseCardDto();

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumberCard(anyString())).thenReturn(false);

        final Card[] savedCard = new Card[1];
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            savedCard[0] = invocation.getArgument(0);
            savedCard[0].setId(TestData.CARD_ID);
            return savedCard[0];
        });

        // When
        ResponseCardDto actualDto = cardServiceImpl.createCard(request);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getCardStatus(), actualDto.getCardStatus());
        assertEquals(BigDecimal.ZERO, savedCard[0].getBalance());
        assertEquals(CardStatus.NO_STATUS, savedCard[0].getCardStatus());
        assertNotNull(savedCard[0].getLimit());
        assertEquals(request.getLimitDto().getDailyLimit(), savedCard[0].getLimit().getDailyLimit());
        assertEquals(request.getLimitDto().getMonthlyLimit(), savedCard[0].getLimit().getMonthlyLimit());
        assertEquals(user, savedCard[0].getUser());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(cardRepository, times(1)).existsByNumberCard(anyString());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void createCardWhenUserNotFound() {
        // Given
        RequestCreatCardDto request = TestData.createTestRequestCreatCardDto();

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> cardServiceImpl.createCard(request));
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(cardRepository, never()).existsByNumberCard(anyString());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCardWhenCardAlreadyExists() throws Exception {
        // Given
        RequestCreatCardDto request = TestData.createTestRequestCreatCardDto();
        User user = TestData.createTestUser();
        String encryptedNumber = EncryptionNumberCard.encrypt(request.getNumberCard());

        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumberCard(encryptedNumber)).thenReturn(true);

        // When & Then
        assertThrows(CardAlreadyExistsException.class, () -> cardServiceImpl.createCard(request));
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(cardRepository, times(1)).existsByNumberCard(encryptedNumber);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void changeStatusCardWhenCardExistsAndStatusActive() {
        // Given
        Card card = TestData.createTestCard();
        card.setCardStatus(CardStatus.NO_STATUS);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        ResponseCardDto actualDto = cardServiceImpl.changeStatusCard(TestData.CARD_ID, "active");

        // Then
        assertNotNull(actualDto);
        assertEquals(TestData.CARD_ID, actualDto.getId());
        assertEquals(CardStatus.ACTIVE, actualDto.getCardStatus());
        assertEquals(CardStatus.ACTIVE, card.getCardStatus());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changeStatusCardWhenCardExistsAndStatusBlocked() {
        // Given
        Card card = TestData.createTestCard();
        card.setCardStatus(CardStatus.NO_STATUS);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        ResponseCardDto actualDto = cardServiceImpl.changeStatusCard(TestData.CARD_ID, "blocked");

        // Then
        assertNotNull(actualDto);
        assertEquals(TestData.CARD_ID, actualDto.getId());
        assertEquals(CardStatus.BLOCKED, actualDto.getCardStatus());
        assertEquals(CardStatus.BLOCKED, card.getCardStatus());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changeStatusCardWhenCardExistsAndStatusInvalid() {
        // Given
        Card card = TestData.createTestCard();
        card.setCardStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        // When
        ResponseCardDto actualDto = cardServiceImpl.changeStatusCard(TestData.CARD_ID, "invalid");

        // Then
        assertNotNull(actualDto);
        assertEquals(TestData.CARD_ID, actualDto.getId());
        assertEquals(CardStatus.NO_STATUS, actualDto.getCardStatus());
        assertEquals(CardStatus.NO_STATUS, card.getCardStatus());
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void changeStatusCardWhenCardNotFound() {
        // Given
        when(cardRepository.findById(TestData.CARD_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardServiceImpl.changeStatusCard(TestData.CARD_ID, "active"));
        verify(cardRepository, times(1)).findById(TestData.CARD_ID);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deleteCardWhenCardExists() {
        // Given
        when(cardRepository.deleteCardByIdCard(TestData.CARD_ID)).thenReturn(1);

        // When
        cardServiceImpl.deleteCard(TestData.CARD_ID);

        // Then
        verify(cardRepository, times(1)).deleteCardByIdCard(TestData.CARD_ID);
    }

    @Test
    void deleteCardWhenCardNotFound() {
        // Given
        when(cardRepository.deleteCardByIdCard(TestData.CARD_ID)).thenReturn(0);

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardServiceImpl.deleteCard(TestData.CARD_ID));
        verify(cardRepository, times(1)).deleteCardByIdCard(TestData.CARD_ID);
    }
}
