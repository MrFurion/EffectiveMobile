package by.effective.mobile.eb.repositories;

import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceCardAndTransactionDataBetween(Card sourceCard, LocalDateTime start, LocalDateTime end);
}
