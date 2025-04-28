package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.entity.Currency;

import java.util.List;

public interface CurrencyRepository {
    List<Currency> findAll();
}
