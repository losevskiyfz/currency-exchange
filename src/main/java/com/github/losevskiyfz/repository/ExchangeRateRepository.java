package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.entity.ExchangeRate;

import java.util.List;

public interface ExchangeRateRepository {

    List<ExchangeRate> findAll();
}
