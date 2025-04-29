package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.entity.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
    List<Currency> findAll();
    Currency findByCode(String code);
}
