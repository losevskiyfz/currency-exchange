package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.mapper.ExchangeRateMapper;
import com.github.losevskiyfz.repository.ExchangeRateRepository;

import java.util.List;
import java.util.logging.Logger;

public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final Logger LOG = Logger.getLogger(ExchangeRateServiceImpl.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateRepository exchangeRateRepository = context.resolve(ExchangeRateRepository.class);
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @Override
    public List<ExchangeRateDto> getAll() {
        LOG.info("Getting all currencies");
        return exchangeRateRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
