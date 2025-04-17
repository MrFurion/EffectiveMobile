package by.effective.mobile.eb.services.impl;

import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.request.RequestWithdrawFromCard;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.enums.CardStatus;
import by.effective.mobile.eb.enums.TransactionType;
import by.effective.mobile.eb.exception.CardNegativeBalanceException;
import by.effective.mobile.eb.exception.CardNotActiveException;
import by.effective.mobile.eb.exception.CardNotFoundException;
import by.effective.mobile.eb.exception.LimitDayNotSetException;
import by.effective.mobile.eb.exception.LimitMonthNotSetException;
import by.effective.mobile.eb.mapper.CardMapper;
import by.effective.mobile.eb.models.BlockReason;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Limit;
import by.effective.mobile.eb.models.Transaction;
import by.effective.mobile.eb.repositories.CardRepository;
import by.effective.mobile.eb.repositories.TransactionRepository;
import by.effective.mobile.eb.security.SecurityContext;
import by.effective.mobile.eb.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static by.effective.mobile.eb.services.constants.Constants.AMOUNT_TO_WITHDRAW_MUST_BE_POSITIVE_GOT;
import static by.effective.mobile.eb.services.constants.Constants.CARD_NOT_FOUND_WITH_ID;
import static by.effective.mobile.eb.services.constants.Constants.CARD_WITH_ID_IS_NOT_ACTIVE;
import static by.effective.mobile.eb.services.constants.Constants.INSUFFICIENT_BALANCE_ON_CARD_WITH_ID_BALANCE_REQUIRED;
import static by.effective.mobile.eb.services.constants.Constants.LIMIT_NOT_SET_FOR_CARD_WITH_ID;
import static by.effective.mobile.eb.services.constants.Constants.TRANSACTION_EXCEEDS_DAILY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED;
import static by.effective.mobile.eb.services.constants.Constants.TRANSACTION_EXCEEDS_MONTHLY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED;
import static by.effective.mobile.eb.services.constants.Constants.USER_NOT_HAVE_CARD_WITH_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public List<ResponseFoundCardDto> findCardsOfUser() {
        return CardMapper.INSTANCE.toResponseFoundCardDto(cardRepository.findCardByUserId(SecurityContext.getUserId()));
    }

    @Transactional
    public void blockedCard(RequestBlockCardDto requestBlockCardDto, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.error(CARD_NOT_FOUND_WITH_ID, cardId);
            return new CardNotFoundException();
        });
        if (!card.getUser().getId().equals(SecurityContext.getUserId())) {
            log.error(USER_NOT_HAVE_CARD_WITH_ID, cardId);
            throw new CardNotFoundException();
        }
        card.setBlockReasons(Set.of(new BlockReason(
                requestBlockCardDto.getReason(),
                LocalDateTime.now(),
                CardStatus.BLOCKED
        )));
    }

    @Transactional
    public void transferBetweenCards(RequestTransactionDto requestTransactionDto) {
        Long userId = SecurityContext.getUserId();
        Card sourceCard = cardRepository.findByIdAndUserId(requestTransactionDto.getSourceCardId(), userId).orElseThrow(() -> {
            log.error(USER_NOT_HAVE_CARD_WITH_ID, userId);
            return new CardNotFoundException();
        });

        Card targetCard = cardRepository.findByIdAndUserId(requestTransactionDto.getTargetCardId(), userId).orElseThrow(() -> {
            log.error(USER_NOT_HAVE_CARD_WITH_ID, userId);
            return new CardNotFoundException();
        });

        if ((sourceCard.getCardStatus() != CardStatus.ACTIVE) || (targetCard.getCardStatus() != CardStatus.ACTIVE)) {
            throw new CardNotActiveException();
        }

        if (sourceCard.getBalance().compareTo(requestTransactionDto.getAmount()) < 0) {
            throw new CardNegativeBalanceException();
        }

        Limit limit = sourceCard.getLimit();
        if (limit == null) {
            log.error(LIMIT_NOT_SET_FOR_CARD_WITH_ID, sourceCard);
            throw new LimitDayNotSetException();
        }

        checkDailyLimit(sourceCard, requestTransactionDto.getAmount(), requestTransactionDto.getSourceCardId(), TransactionType.TRANSFER);
        checkMonthlyLimit(sourceCard, requestTransactionDto.getAmount(), requestTransactionDto.getSourceCardId(), TransactionType.TRANSFER);

        sourceCard.setBalance(sourceCard.getBalance().subtract(requestTransactionDto.getAmount()));
        targetCard.setBalance(targetCard.getBalance().add(requestTransactionDto.getAmount()));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        Transaction transaction = new Transaction();
        transaction.setSourceCard(sourceCard);
        transaction.setTargetCard(targetCard);
        transaction.setAmount(requestTransactionDto.getAmount());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionData(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void addBalance(Long idCard, BigDecimal amount) {
        Card targetCard = cardRepository.findById(idCard).orElseThrow(() -> {
            log.error(CARD_NOT_FOUND_WITH_ID, idCard);
            return new CardNotFoundException();
        });

        Transaction transaction = new Transaction();
        transaction.setTargetCard(targetCard);
        transaction.setSourceCard(targetCard);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionData(LocalDateTime.now());
        targetCard.setBalance(targetCard.getBalance().add(amount));
        cardRepository.save(targetCard);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdrawFromCard(RequestWithdrawFromCard requestWithdrawFromCard) {
        Long userId = SecurityContext.getUserId();

        Card card = cardRepository.findByIdAndUserId(requestWithdrawFromCard.getCardId(), userId)
                .orElseThrow(() -> {
                    log.error(CARD_NOT_FOUND_WITH_ID, requestWithdrawFromCard.getCardId());
                    return new CardNotFoundException();
                });

        if (requestWithdrawFromCard.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error(AMOUNT_TO_WITHDRAW_MUST_BE_POSITIVE_GOT, requestWithdrawFromCard.getAmount());
            throw new CardNegativeBalanceException();
        }

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            log.error(CARD_WITH_ID_IS_NOT_ACTIVE, requestWithdrawFromCard.getCardId());
            throw new CardNotActiveException();
        }
        if (card.getBalance().compareTo(requestWithdrawFromCard.getAmount()) < 0) {
            log.error(INSUFFICIENT_BALANCE_ON_CARD_WITH_ID_BALANCE_REQUIRED,
                    requestWithdrawFromCard.getCardId(), card.getBalance(), requestWithdrawFromCard.getAmount());
            throw new CardNegativeBalanceException();
        }

        Limit limit = card.getLimit();
        if (limit == null) {
            log.error(LIMIT_NOT_SET_FOR_CARD_WITH_ID, requestWithdrawFromCard.getCardId());
            throw new LimitDayNotSetException();
        }

        checkDailyLimit(card, requestWithdrawFromCard.getAmount(), requestWithdrawFromCard.getCardId(), TransactionType.DEBIT);
        checkMonthlyLimit(card, requestWithdrawFromCard.getAmount(), requestWithdrawFromCard.getCardId(), TransactionType.DEBIT);

        card.setBalance(card.getBalance().subtract(requestWithdrawFromCard.getAmount()));
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setSourceCard(card);
        transaction.setAmount(requestWithdrawFromCard.getAmount());
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionData(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private void checkDailyLimit(Card card, BigDecimal amount, Long cardId, TransactionType transactionType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

        BigDecimal dailySpent = transactionRepository.findBySourceCardAndTransactionDataBetween(
                        card,
                        dayStart,
                        dayEnd
                ).stream()
                .filter(tx -> tx.getTransactionType() == transactionType)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Limit limit = card.getLimit();
        if (dailySpent.add(amount).compareTo(limit.getDailyLimit()) > 0) {
            log.error(TRANSACTION_EXCEEDS_DAILY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED,
                    cardId, dailySpent, limit.getDailyLimit(), amount);
            throw new LimitDayNotSetException();
        }
    }

    private void checkMonthlyLimit(Card card, BigDecimal amount, Long cardId, TransactionType transactionType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        BigDecimal monthlySpent = transactionRepository.findBySourceCardAndTransactionDataBetween(
                        card,
                        monthStart,
                        monthEnd
                ).stream()
                .filter(tx -> tx.getTransactionType() == transactionType)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Limit limit = card.getLimit();
        if (monthlySpent.add(amount).compareTo(limit.getMonthlyLimit()) > 0) {
            log.error(TRANSACTION_EXCEEDS_MONTHLY_LIMIT_FOR_CARD_WITH_ID_SPENT_LIMIT_REQUESTED,
                    cardId, monthlySpent, limit.getMonthlyLimit(), amount);
            throw new LimitMonthNotSetException();
        }
    }
}
