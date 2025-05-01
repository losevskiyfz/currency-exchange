package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.PostExchangeRate;
import com.github.losevskiyfz.entity.ExchangeRate;
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

    @Override
    public ExchangeRateDto getExchangeRateBySourceAndTargetCode(String sourceCode, String targetCode) {
        LOG.info("Getting exchange rate by source and target codes");
        ExchangeRate exchangeRate = exchangeRateRepository.findBySourceAndTargetCode(
                sourceCode, targetCode
        );
        return mapper.toDto(exchangeRate);
    }

    @Override
    public ExchangeRateDto save(PostExchangeRate postExchangeRate) {
        LOG.info("Posting exchange rate");
        ExchangeRateDto exchangeRateDto = mapper.toDto(postExchangeRate);
        ExchangeRate exchangeRate = mapper.toEntity(exchangeRateDto);
        ExchangeRate res = exchangeRateRepository.save(exchangeRate);
        return mapper.toDto(res);
    }

    @Override
    public ExchangeRateDto update(ExchangeRateDto exchangeRateDto) {
        LOG.info("Patching exchange rate");
        ExchangeRate exchangeRate = mapper.toEntity(exchangeRateDto);
        ExchangeRate res = exchangeRateRepository.update(exchangeRate);
        return mapper.toDto(res);
    }
}
