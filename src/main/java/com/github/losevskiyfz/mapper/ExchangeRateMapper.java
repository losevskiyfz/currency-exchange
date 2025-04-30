package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "rate", target = "rate")
    ExchangeRateDto toDto(ExchangeRate exchangeRate);
}
