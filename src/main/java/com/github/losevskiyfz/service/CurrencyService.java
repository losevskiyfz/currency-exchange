package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final Logger logger = Logger.getLogger(CurrencyService.class.getName());
    private final CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);

    public List<CurrencyDto> getAllCurrencies() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<Currency> currencies = em.createQuery("SELECT c FROM Currency c", Currency.class)
                    .getResultList();
            tx.commit();
            return currencies.stream()
                    .map(currencyMapper::currencyToCurrencyDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        return Collections.emptyList();
    }

    public List<CurrencyDto> getCurrencyByCode(String code) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<Currency> currencies = em.createQuery("SELECT c FROM Currency c WHERE c.code = :code", Currency.class)
                    .setParameter("code", code)
                    .getResultList();
            tx.commit();
            return currencies.stream()
                    .map(currencyMapper::currencyToCurrencyDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        return Collections.emptyList();
    }

    public CurrencyDto saveCurrency(CurrencyDto currencyDto) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Currency currency = currencyMapper.currencyDtoToCurrency(currencyDto);
            em.persist(currency);
            tx.commit();
            return currencyMapper.currencyToCurrencyDto(currency);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        return currencyDto;
    }
}