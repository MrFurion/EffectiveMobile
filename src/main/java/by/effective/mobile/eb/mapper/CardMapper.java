package by.effective.mobile.eb.mapper;

import by.effective.mobile.eb.dto.request.RequestCreatCardDto;

import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.models.Card;
import by.effective.mobile.eb.util.EncryptionNumberCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    Card toCard(RequestCreatCardDto requestCardDto);
    ResponseCardDto toResponseCardDto(Card card);
    @Mapping(source = "numberCard", target = "numberCard", qualifiedByName = "decryptCardNumber")
    ResponseFoundCardDto toResponseFoundCardDto(Card card);

    @Named("decryptCardNumber")
    default String decryptCardNumber(String encryptedNumber) {
        try {
            return EncryptionNumberCard.decrypt(encryptedNumber);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting the card number: " + e.getMessage(), e);
        }
    }
    List<ResponseFoundCardDto> toResponseFoundCardDto(List<Card> cards);
}
