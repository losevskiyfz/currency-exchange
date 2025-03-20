package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.ExchangeRateDto;
import com.github.losevskiyfz.entity.ExchangeRate;
import com.github.losevskiyfz.mapper.ExchangeRateMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private static final Logger logger = Logger.getLogger(ExchangeRateService.class.getName());
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);

    public List<ExchangeRateDto> getAllExchangeRates() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<ExchangeRate> exchangeRates = em
                    .createQuery("""
                                SELECT r
                                FROM ExchangeRate r
                            """, ExchangeRate.class)
                    .getResultList();
            tx.commit();
            return exchangeRates.stream()
                    .map(exchangeRateMapper::exchangeRateToExchangeRateDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            em.close();
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return Collections.emptyList();
    }

    public Optional<ExchangeRateDto> getExchangeRates(String baseCode, String targetCode) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<ExchangeRate> exchangeRates = em
                    .createQuery("""
                                SELECT r
                                FROM ExchangeRate r
                                WHERE r.baseCurrency.code = :baseCode
                                AND r.targetCurrency.code = :targetCode
                            """, ExchangeRate.class)
                    .getResultList();
            tx.commit();
            return exchangeRates.stream()
                    .findFirst()
                    .map(exchangeRateMapper::exchangeRateToExchangeRateDto);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            em.close();
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return Optional.empty();
    }

    public ExchangeRateDto saveExchangeRate(ExchangeRateDto exchangeRateDto) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ExchangeRate exchangeRate = exchangeRateMapper.exchangeRateDtoToExchangeRate(exchangeRateDto);
            em.persist(exchangeRate);
            tx.commit();
            return exchangeRateMapper.exchangeRateToExchangeRateDto(exchangeRate);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            em.close();
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return exchangeRateDto;
    }
}
