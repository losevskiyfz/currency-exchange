package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.entity.ExchangeRate;
import com.github.losevskiyfz.mapper.ExchangeRateMapper;
import com.github.losevskiyfz.repository.ExchangeRateRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ExchangeRateRepository exchangeRateRepository = context.resolve(ExchangeRateRepository.class);
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    public List<ExchangeRateDto> getAll() throws SQLException, InterruptedException {
        return exchangeRateRepository.findAll().stream()
                .map(mapper::toExchangeRateDto)
                .collect(Collectors.toList());
    }

    public Optional<ExchangeRateDto> getByCodes(String baseCode, String targetCode) throws SQLException, InterruptedException {
        return exchangeRateRepository
                .findByBaseCodeAndTargetCode(baseCode, targetCode)
                .map(mapper::toExchangeRateDto);
    }

    public ExchangeRateDto create(ExchangeRateDto exchangeRateDto) throws SQLException, InterruptedException {
        ExchangeRate savedExchangeRate = exchangeRateRepository.save(mapper.toExchangeRate(exchangeRateDto));
        return mapper.toExchangeRateDto(savedExchangeRate);
    }

    public ExchangeRateDto update(ExchangeRateDto exchangeRateDto) throws SQLException, InterruptedException {
        ExchangeRate updatedExchangeRate = exchangeRateRepository.update(mapper.toExchangeRate(exchangeRateDto));
        return mapper.toExchangeRateDto(updatedExchangeRate);
    }
}
