package com.github.losevskiyfz.service;

import com.github.losevskiyfz.dto.ExchangeRateDto;

import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDto> getAll();
}
