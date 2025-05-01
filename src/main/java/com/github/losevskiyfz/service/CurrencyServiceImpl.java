package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.dto.PostCurrency;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import com.github.losevskiyfz.repository.CurrencyRepository;

import java.util.List;
import java.util.logging.Logger;

public class CurrencyServiceImpl implements CurrencyService {
    private static final Logger LOG = Logger.getLogger(CurrencyServiceImpl.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final CurrencyRepository currencyRepository = context.resolve(CurrencyRepository.class);
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    @Override
    public List<CurrencyDto> getAll() {
        LOG.info("Getting all currencies");
        return currencyRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CurrencyDto getByCode(String code) {
        LOG.info("Getting currency by code");
        return mapper.toDto(currencyRepository.findByCode(code));
    }

    @Override
    public CurrencyDto save(PostCurrency postCurrency) {
        LOG.info("Saving currency");
        CurrencyDto curDto = mapper.toDto(postCurrency);
        Currency cur = mapper.toEntity(curDto);
        return mapper.toDto(currencyRepository.save(cur));
    }
}
