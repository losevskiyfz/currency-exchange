package com.github.losevskiyfz.service;

import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.PostExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDto> getAll();

    ExchangeRateDto getExchangeRateBySourceAndTargetCode(String sourceCode, String targetCode);

    ExchangeRateDto save(PostExchangeRate postExchangeRate);
}
