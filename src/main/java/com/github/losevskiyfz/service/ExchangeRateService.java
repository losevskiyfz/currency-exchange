package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.dto.ExchangeDto;
import com.github.losevskiyfz.entity.ExchangeRate;
import com.github.losevskiyfz.mapper.ExchangeRateMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.losevskiyfz.utils.CurrencyUtils.convertAmount;

public class ExchangeRateService {
    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);

    private <T> T executeInTransaction(TransactionTask<T> function) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = function.execute(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            logger.severe("Transaction failed: " + e.getMessage());
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
        throw new RuntimeException("Error executing transaction");
    }

    public List<ExchangeRateDto> getAllExchangeRates() {
        return executeInTransaction(em -> {
            List<ExchangeRate> exchangeRates = em.createQuery("SELECT r FROM ExchangeRate r", ExchangeRate.class)
                    .getResultList();
            return exchangeRates.stream()
                    .map(exchangeRateMapper::exchangeRateToExchangeRateDto)
                    .collect(Collectors.toList());
        });
    }

    public Optional<ExchangeRateDto> getExchangeRate(String baseCode, String targetCode) {
        return executeInTransaction(em -> {
            List<ExchangeRate> exchangeRates = em.createQuery(
                            "SELECT r FROM ExchangeRate r WHERE r.baseCurrency.code = :baseCode AND r.targetCurrency.code = :targetCode",
                            ExchangeRate.class)
                    .setParameter("baseCode", baseCode)
                    .setParameter("targetCode", targetCode)
                    .getResultList();
            return exchangeRates.stream()
                    .findFirst()
                    .map(exchangeRateMapper::exchangeRateToExchangeRateDto);
        });
    }

    public ExchangeRateDto saveExchangeRate(ExchangeRateDto exchangeRateDto) {
        return executeInTransaction(em -> {
            ExchangeRate exchangeRate = exchangeRateMapper.exchangeRateDtoToExchangeRate(exchangeRateDto);
            em.persist(exchangeRate);
            return exchangeRateMapper.exchangeRateToExchangeRateDto(exchangeRate);
        });
    }

    public ExchangeRateDto updateExchangeRate(ExchangeRateDto exchangeRateDto) {
        return executeInTransaction(em -> {
            ExchangeRate exchangeRate = exchangeRateMapper.exchangeRateDtoToExchangeRate(exchangeRateDto);
            em.merge(exchangeRate);
            return exchangeRateMapper.exchangeRateToExchangeRateDto(exchangeRate);
        });
    }

    public Optional<ExchangeDto> exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) {
        return executeInTransaction(em -> {
            Optional<ExchangeRateDto> exchangeRateDtoOpt = getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            return exchangeRateDtoOpt.map(exchangeRateDto -> {
                ExchangeDto exchangeDto = ExchangeDto.builder()
                        .baseCurrency(exchangeRateDto.getBaseCurrency())
                        .targetCurrency(exchangeRateDto.getTargetCurrency())
                        .rate(exchangeRateDto.getRate())
                        .amount(new BigDecimal(amount))
                        .convertedAmount(
                                convertAmount(exchangeRateDto.getRate(), amount)
                        )
                        .build();
                return exchangeDto;
            });
        });
    }
}
