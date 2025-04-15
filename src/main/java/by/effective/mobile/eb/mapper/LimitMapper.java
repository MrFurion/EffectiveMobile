package by.effective.mobile.eb.mapper;

import by.effective.mobile.eb.dto.request.LimitDto;
import by.effective.mobile.eb.models.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LimitMapper {
    LimitMapper INSTANCE = Mappers.getMapper(LimitMapper.class);

    Limit toLimit(LimitDto limitDto);
    LimitDto toLimitDto(Limit limit);
}
