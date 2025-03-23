package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import com.github.losevskiyfz.repository.CurrencyRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CurrencyService {
    private final Logger logger = Logger.getLogger(CurrencyService.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyRepository currencyRepository = context.resolve(CurrencyRepository.class);
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    public List<CurrencyDto> getAll() throws SQLException, InterruptedException {
        return currencyRepository.findAll().stream()
                .map(mapper::toCurrencyDto)
                .collect(Collectors.toList());
    }

    public Optional<CurrencyDto> getByCode(String code) throws SQLException, InterruptedException {
        return currencyRepository.findByCode(code).map(mapper::toCurrencyDto);
    }

    public CurrencyDto create(CurrencyDto currencyDto) throws SQLException, InterruptedException {
        Currency savedCurrency = currencyRepository.save(mapper.toCurrency(currencyDto));
        return mapper.toCurrencyDto(savedCurrency);
    }
}
