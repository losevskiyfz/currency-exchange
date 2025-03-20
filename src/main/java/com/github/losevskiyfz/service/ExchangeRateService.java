package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
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

public class ExchangeRateService {
    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);
    private final ExchangeService exchangeService = context.resolve(ExchangeService.class);
    private final CurrencyService currencyService = context.resolve(CurrencyService.class);

    public List<ExchangeRateDto> getAllExchangeRates() {
        logger.info("getAllExchangeRates()");
        return executeInTransaction(em -> {
            List<ExchangeRate> exchangeRates = em.createQuery("SELECT r FROM ExchangeRate r", ExchangeRate.class)
                    .getResultList();
            return exchangeRates.stream()
                    .map(exchangeRateMapper::exchangeRateToExchangeRateDto)
                    .collect(Collectors.toList());
        });
    }

    public Optional<ExchangeRateDto> getExchangeRate(String baseCode, String targetCode) {
        logger.info("getExchangeRate(baseCode,targetCode), baseCode: " + baseCode + ", targetCode: " + targetCode);
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
        logger.info("saveExchangeRate(exchangeRateDto), exchangeRateDto: " + exchangeRateDto);
        return executeInTransaction(em -> {
            ExchangeRate exchangeRate = exchangeRateMapper.exchangeRateDtoToExchangeRate(exchangeRateDto);
            em.persist(exchangeRate);
            return exchangeRateMapper.exchangeRateToExchangeRateDto(exchangeRate);
        });
    }

    public ExchangeRateDto updateExchangeRate(ExchangeRateDto exchangeRateDto) {
        logger.info("updateExchangeRate(exchangeRateDto), exchangeRateDto: " + exchangeRateDto);
        return executeInTransaction(em -> {
            ExchangeRate exchangeRate = exchangeRateMapper.exchangeRateDtoToExchangeRate(exchangeRateDto);
            em.merge(exchangeRate);
            return exchangeRateMapper.exchangeRateToExchangeRateDto(exchangeRate);
        });
    }

    public Optional<ExchangeDto> exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) {
        logger.info("excahnge(baseCurrencyCode,targetCurrencyCode,amount), " +
                "baseCurrencyCode: " + baseCurrencyCode + ", " +
                "targetCurrencyCode: " + targetCurrencyCode);
        return executeInTransaction(em -> {
            Optional<ExchangeRateDto> exchangeRateDirectOpt = getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (exchangeRateDirectOpt.isPresent()) {
                logger.info("Direct converting");
                return Optional.of(exchangeService.exchangeDirect(exchangeRateDirectOpt.get(), amount));
            }
            Optional<ExchangeRateDto> exchangeRateReverseOpt = getExchangeRate(targetCurrencyCode, baseCurrencyCode);
            if (exchangeRateReverseOpt.isPresent()) {
                logger.info("Reverse converting");
                return Optional.of(exchangeService.exchangeReverse(exchangeRateReverseOpt.get(), amount));
            } else {
                logger.info("Try cross converting");
                Optional<CurrencyDto> baseCur = currencyService.getCurrencyByCode(baseCurrencyCode);
                Optional<CurrencyDto> targetCur = currencyService.getCurrencyByCode(targetCurrencyCode);
                if (baseCur.isEmpty() || targetCur.isEmpty()) {
                    return Optional.empty();
                }
                return exchangeService.convert(getAllExchangeRates(), baseCur.get(), targetCur.get(), new BigDecimal(amount));
            }
        });
    }

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
        RuntimeException runtimeException = new RuntimeException("Transaction failed");
        logger.severe("Trowing exception: " + runtimeException.getMessage());
        throw runtimeException;
    }
}
