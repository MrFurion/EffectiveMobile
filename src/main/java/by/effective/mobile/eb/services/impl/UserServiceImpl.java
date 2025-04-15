package by.effective.mobile.eb.services.impl;

import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
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

import static by.effective.mobile.eb.services.constants.Constants.CARD_NOT_FOUND_WITH_ID;
import static by.effective.mobile.eb.services.constants.Constants.LIMIT_NOT_SET_FOR_CARD_WITH_ID;
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

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        BigDecimal dailySpent = transactionRepository.findBySourceCardAndTransactionDataBetween(
                        sourceCard,
                        dayStart,
                        dayEnd
                ).stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.TRANSFER)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dailySpent.add(requestTransactionDto.getAmount()).compareTo(limit.getDailyLimit()) > 0) {
            throw new LimitDayNotSetException();
        }

        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();
        BigDecimal monthlySpent = transactionRepository.findBySourceCardAndTransactionDataBetween(
                        sourceCard,
                        monthStart,
                        monthEnd
                ).stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.TRANSFER)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (monthlySpent.add(requestTransactionDto.getAmount()).compareTo(limit.getMonthlyLimit()) > 0) {
            throw new LimitMonthNotSetException();
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(requestTransactionDto.getAmount()));
        targetCard.setBalance(targetCard.getBalance().add(requestTransactionDto.getAmount()));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        Transaction transaction = new Transaction();
        transaction.setSourceCard(sourceCard);
        transaction.setTargetCard(targetCard);
        transaction.setAmount(requestTransactionDto.getAmount());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionData(now);
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
}
