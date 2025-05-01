package com.github.losevskiyfz.service;

import com.github.losevskiyfz.dto.ExchangeDto;

public interface ExchangeService {
    ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, String amount);
}
