package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.PatchExchangeRate;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "rate", target = "rate")
    ExchangeRateDto toExchangeRateDto(ExchangeRate exchangeRate);

    @Mapping(source = "rate", target = "rate")
    ExchangeRate toExchangeRate(ExchangeRateDto exchangeRateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseCurrency", source = "baseCurrencyCode", qualifiedByName = "currencyFromCode")
    @Mapping(target = "targetCurrency", source = "targetCurrencyCode", qualifiedByName = "currencyFromCode")
    ExchangeRateDto toExchangeRateDto(PostExchangeRate postExchangeRate);

    @Mapping(target = "baseCurrency", source = "baseCurrency", qualifiedByName = "currencyFromCode")
    @Mapping(target = "targetCurrency", source = "targetCurrency", qualifiedByName = "currencyFromCode")
    @Mapping(target = "rate", source = "patchExchangeRate.rate")
    ExchangeRateDto toExchangeRateDto(PatchExchangeRate patchExchangeRate, String baseCurrency, String targetCurrency);

    @Named("currencyFromCode")
    default CurrencyDto currencyFromCode(String code) {
        return CurrencyDto.builder()
                .code(code)
                .build();
    }
}