package com.github.losevskiyfz.mapper;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.entity.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source = "fullName", target = "name")
    CurrencyDto toDto(Currency currency);
}