package by.effective.mobile.eb.repositories;

import by.effective.mobile.eb.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumberCard(String numberCard);

    @Modifying
    @Query(value = "DELETE FROM limits WHERE card_id = :id; " +
                   "DELETE FROM cards WHERE id = :id;", nativeQuery = true)
    int deleteCardByIdCard(Long id);

    List<Card> findCardByUserId(Long userId);

    Optional<Card> findByIdAndUserId(Long cardId, Long userId);
}
