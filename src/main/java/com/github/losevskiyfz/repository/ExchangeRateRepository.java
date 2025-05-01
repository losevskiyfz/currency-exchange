package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.entity.ExchangeRate;

import java.util.List;

public interface ExchangeRateRepository {

    List<ExchangeRate> findAll();

    ExchangeRate findBySourceAndTargetCode(String sourceCode, String targetCode);
}
