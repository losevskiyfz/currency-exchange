package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "rate", target = "rate", qualifiedByName = "bigDecimalToString")
    ExchangeRateDto exchangeRateToExchangeRateDto(ExchangeRate exchangeRate);

    @Mapping(source = "rate", target = "rate", qualifiedByName = "stringToBigDecimal")
    ExchangeRate exchangeRateDtoToExchangeRate(ExchangeRateDto exchangeRateDto);

    @org.mapstruct.Named("bigDecimalToString")
    default String bigDecimalToString(BigDecimal value) {
        return value != null ? value.toString() : null;
    }

    @org.mapstruct.Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        return value != null ? new BigDecimal(value) : null;
    }
}

