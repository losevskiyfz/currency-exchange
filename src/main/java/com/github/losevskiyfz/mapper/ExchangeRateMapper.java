package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRateDto toDto(ExchangeRate exchangeRate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseCurrency", source = "baseCurrencyCode", qualifiedByName = "currencyFromCode")
    @Mapping(target = "targetCurrency", source = "targetCurrencyCode", qualifiedByName = "currencyFromCode")
    ExchangeRateDto toDto(PostExchangeRate postExchangeRate);

    ExchangeRate toEntity(ExchangeRateDto exchangeRateDto);

    @Named("currencyFromCode")
    default CurrencyDto currencyFromCode(String code) {
        return CurrencyDto.builder()
                .code(code)
                .build();
    }
}
