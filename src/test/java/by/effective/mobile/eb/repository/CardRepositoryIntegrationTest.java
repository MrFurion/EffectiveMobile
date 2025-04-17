package by.effective.mobile.eb.repository;

import by.effective.mobile.eb.repositories.CardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("testcontainers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})
class CardRepositoryIntegrationTest {

    @Autowired
    private CardRepository cardRepository;

    @Test
    void deleteCardByIdCardWhenCardExists() {
        // Given
        Long cardId = 1L;
        int expectedDeletedCount = 1;

        assertTrue(cardRepository.findById(cardId).isPresent());

        // When
        int actualDeletedCount = cardRepository.deleteCardByIdCard(cardId);

        // Then
        assertEquals(expectedDeletedCount, actualDeletedCount);
        assertFalse(cardRepository.findById(cardId).isPresent());
    }
}
