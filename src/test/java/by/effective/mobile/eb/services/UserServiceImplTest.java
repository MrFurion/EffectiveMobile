package by.effective.mobile.eb.services;

import by.effective.mobile.eb.data.TestData;
import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.request.RequestWithdrawFromCard;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.enums.CardStatus;
import by.effective.mobile.eb.enums.TransactionType;
import by.effective.mobile.eb.exception.CardNotFoundException;
import by.effective.mobile.eb.models.BlockReason;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Transaction;
import by.effective.mobile.eb.repositories.CardRepository;
import by.effective.mobile.eb.repositories.TransactionRepository;
import by.effective.mobile.eb.security.SecurityContext;
import by.effective.mobile.eb.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void findCardsOfUserWhenCardsExist() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long userId = TestData.USER_ID;
            Card card = TestData.createTestCard();
            List<Card> cards = List.of(card);
            ResponseFoundCardDto expectedDto = TestData.createTestResponseFoundCardDto();

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findCardByUserId(userId)).thenReturn(cards);

            // When
            List<ResponseFoundCardDto> result = userService.findCardsOfUser();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            ResponseFoundCardDto actualDto = result.get(0);
            assertEquals(expectedDto.getNumberCard(), actualDto.getNumberCard());
            assertEquals(expectedDto.getExpiryDate(), actualDto.getExpiryDate());
            assertEquals(expectedDto.getCardStatus(), actualDto.getCardStatus());
            assertEquals(expectedDto.getBalance(), actualDto.getBalance());
            assertEquals(expectedDto.getTransactions(), actualDto.getTransactions());
            assertEquals(expectedDto.getLimit().getDailyLimit(), actualDto.getLimit().getDailyLimit());
            assertEquals(expectedDto.getLimit().getMonthlyLimit(), actualDto.getLimit().getMonthlyLimit());
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findCardByUserId(userId);
        }
    }

    @Test
    void findCardsOfUserWhenNoCardsExist() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long userId = TestData.USER_ID;

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findCardByUserId(userId)).thenReturn(Collections.emptyList());

            // When
            List<ResponseFoundCardDto> result = userService.findCardsOfUser();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findCardByUserId(userId);
        }
    }

    @Test
    void blockedCardWhenCardExistsAndBelongsToUser() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long cardId = TestData.CARD_ID;
            Long userId = TestData.USER_ID;
            Card card = TestData.createTestCard();
            RequestBlockCardDto request = new RequestBlockCardDto();
            request.setReason("Lost card");

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

            // When
            userService.blockedCard(request, cardId);

            // Then
            assertNotNull(card.getBlockReasons());
            assertEquals(1, card.getBlockReasons().size());
            BlockReason blockReason = card.getBlockReasons().iterator().next();
            assertEquals(request.getReason(), blockReason.getReason());
            assertEquals(CardStatus.BLOCKED, blockReason.getStatus());
            assertNotNull(blockReason.getRequestDate());
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findById(cardId);
        }
    }

    @Test
    void blockedCardWhenCardNotFound() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long cardId = TestData.CARD_ID;
            Long userId = TestData.USER_ID;
            RequestBlockCardDto request = new RequestBlockCardDto();
            request.setReason("Lost card");

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(CardNotFoundException.class, () -> userService.blockedCard(request, cardId));
            mockedSecurityContext.verify(SecurityContext::getUserId, times(0));
            verify(cardRepository, times(1)).findById(cardId);
        }
    }

    @Test
    void blockedCardWhenCardDoesNotBelongToUser() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long cardId = TestData.CARD_ID;
            Long userId = TestData.USER_ID;
            Long differentUserId = 999L;
            Card card = TestData.createTestCard();
            card.getUser().setId(differentUserId);
            RequestBlockCardDto request = new RequestBlockCardDto();
            request.setReason("Lost card");

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

            // When & Then
            assertThrows(CardNotFoundException.class, () -> userService.blockedCard(request, cardId));
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findById(cardId);
        }
    }

    @Test
    void transferBetweenCardsWhenAllConditionsMet() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long userId = TestData.USER_ID;
            Long sourceCardId = TestData.CARD_ID;
            Long targetCardId = 2L;
            BigDecimal transferAmount = new BigDecimal("500.00");

            RequestTransactionDto request = new RequestTransactionDto();
            request.setSourceCardId(sourceCardId);
            request.setTargetCardId(targetCardId);
            request.setAmount(transferAmount);

            Card sourceCard = TestData.createTestCard();
            sourceCard.setId(sourceCardId);
            sourceCard.setCardStatus(CardStatus.ACTIVE);
            sourceCard.setBalance(new BigDecimal("1000.00"));

            Card targetCard = TestData.createTestCard();
            targetCard.setId(targetCardId);
            targetCard.setCardStatus(CardStatus.ACTIVE);
            targetCard.setBalance(new BigDecimal("200.00"));

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findByIdAndUserId(sourceCardId, userId)).thenReturn(Optional.of(sourceCard));
            when(cardRepository.findByIdAndUserId(targetCardId, userId)).thenReturn(Optional.of(targetCard));
            when(cardRepository.save(sourceCard)).thenReturn(sourceCard);
            when(cardRepository.save(targetCard)).thenReturn(targetCard);
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            userService.transferBetweenCards(request);

            // Then
            assertEquals(new BigDecimal("500.00"), sourceCard.getBalance());
            assertEquals(new BigDecimal("700.00"), targetCard.getBalance());
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findByIdAndUserId(sourceCardId, userId);
            verify(cardRepository, times(1)).findByIdAndUserId(targetCardId, userId);
            verify(cardRepository, times(1)).save(sourceCard);
            verify(cardRepository, times(1)).save(targetCard);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }

    @Test
    void addBalanceWhenCardExists() {
        // Given
        Long cardId = TestData.CARD_ID;
        BigDecimal amountToAdd = new BigDecimal("300.00");
        Card targetCard = TestData.createTestCard();
        targetCard.setBalance(new BigDecimal("500.00"));

        // When
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(targetCard));
        when(cardRepository.save(targetCard)).thenReturn(targetCard);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        userService.addBalance(cardId, amountToAdd);

        // Then
        assertEquals(new BigDecimal("800.00"), targetCard.getBalance());
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardRepository, times(1)).save(targetCard);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void withdrawFromCardWhenAllConditionsMet() {
        try (MockedStatic<SecurityContext> mockedSecurityContext = mockStatic(SecurityContext.class)) {
            // Given
            Long userId = TestData.USER_ID;
            Long cardId = TestData.CARD_ID;
            BigDecimal withdrawAmount = new BigDecimal("300.00");

            RequestWithdrawFromCard request = new RequestWithdrawFromCard();
            request.setCardId(cardId);
            request.setAmount(withdrawAmount);

            Card card = TestData.createTestCard();
            card.setCardStatus(CardStatus.ACTIVE);
            card.setBalance(new BigDecimal("1000.00"));

            mockedSecurityContext.when(SecurityContext::getUserId).thenReturn(userId);
            when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));
            when(cardRepository.save(card)).thenReturn(card);
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            userService.withdrawFromCard(request);

            // Then
            assertEquals(new BigDecimal("700.00"), card.getBalance());
            mockedSecurityContext.verify(SecurityContext::getUserId, times(1));
            verify(cardRepository, times(1)).findByIdAndUserId(cardId, userId);
            verify(cardRepository, times(1)).save(card);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }

    @Test
    void checkDailyLimitWhenLimitNotExceeded() throws Exception {
        // Given
        Card card = TestData.createTestCard();
        BigDecimal amount = new BigDecimal("200.00");
        Long cardId = TestData.CARD_ID;
        TransactionType transactionType = TransactionType.DEBIT;

        LocalDateTime now = LocalDateTime.of(2025, 4, 17, 12, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(now);
            LocalDate today = now.toLocalDate();
            LocalDateTime dayStart = today.atStartOfDay();
            LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

            Transaction transaction = new Transaction();
            transaction.setAmount(new BigDecimal("100.00"));
            transaction.setTransactionType(transactionType);
            List<Transaction> transactions = List.of(transaction);

            when(transactionRepository.findBySourceCardAndTransactionDataBetween(card, dayStart, dayEnd))
                    .thenReturn(transactions);

            Method checkDailyLimitMethod = UserServiceImpl.class.getDeclaredMethod(
                    "checkDailyLimit", Card.class, BigDecimal.class, Long.class, TransactionType.class);
            checkDailyLimitMethod.setAccessible(true);

            // When
            checkDailyLimitMethod.invoke(userService, card, amount, cardId, transactionType);
            verify(transactionRepository, times(1))
                    .findBySourceCardAndTransactionDataBetween(card, dayStart, dayEnd);
        }
    }

    @Test
    void checkMonthlyLimitWhenLimitNotExceeded() throws Exception {
        // Given
        Card card = TestData.createTestCard();
        BigDecimal amount = new BigDecimal("500.00");
        Long cardId = TestData.CARD_ID;
        TransactionType transactionType = TransactionType.DEBIT;
        LocalDateTime now = LocalDateTime.of(2025, 4, 17, 12, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(now);
            LocalDate today = now.toLocalDate();
            LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

            Transaction transaction = new Transaction();
            transaction.setAmount(new BigDecimal("200.00"));
            transaction.setTransactionType(transactionType);
            List<Transaction> transactions = List.of(transaction);

            when(transactionRepository.findBySourceCardAndTransactionDataBetween(card, monthStart, monthEnd))
                    .thenReturn(transactions);

            Method checkMonthlyLimitMethod = UserServiceImpl.class.getDeclaredMethod(
                    "checkMonthlyLimit", Card.class, BigDecimal.class, Long.class, TransactionType.class);
            checkMonthlyLimitMethod.setAccessible(true);

            // When
            checkMonthlyLimitMethod.invoke(userService, card, amount, cardId, transactionType);

            // Then
            verify(transactionRepository, times(1))
                    .findBySourceCardAndTransactionDataBetween(card, monthStart, monthEnd);
        }
    }
}
