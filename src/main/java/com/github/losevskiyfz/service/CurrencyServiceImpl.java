package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import com.github.losevskiyfz.repository.CurrencyRepository;

import java.util.List;

public class CurrencyServiceImpl implements CurrencyService {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyRepository currencyRepository = context.resolve(CurrencyRepository.class);
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    @Override
    public List<CurrencyDto> getAll() {
        return currencyRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
