package com.github.losevskiyfz.service;

import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.PostCurrency;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDto> getAll();

    CurrencyDto getByCode(String code);

    CurrencyDto save(PostCurrency postCurrency);
}
