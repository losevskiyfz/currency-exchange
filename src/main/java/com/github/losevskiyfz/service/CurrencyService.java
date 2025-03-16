package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            List<Currency> currencies = em.createQuery("SELECT c FROM Currency c", Currency.class)
                    .getResultList();
            em.getTransaction().commit();
            return currencies.stream()
                    .map(currencyMapper::currencyToCurrencyDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return Collections.emptyList();
    }
}