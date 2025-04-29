package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.entity.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source = "fullName", target = "name")
    CurrencyDto toDto(Currency currency);

    @Mapping(source = "name", target = "fullName")
    @Mapping(source = "code", target = "code", qualifiedByName = "toUpperCase")
    Currency toEntity(CurrencyDto currencyDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "code", target = "code", qualifiedByName = "toUpperCase")
    CurrencyDto toDto(PostCurrency postCurrency);

    @Named("toUpperCase")
    default String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }
}