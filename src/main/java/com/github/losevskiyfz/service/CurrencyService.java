package com.github.losevskiyfz.service;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.dto.CurrencyDto;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.mapper.CurrencyMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final Logger logger = Logger.getLogger(CurrencyService.class.getName());
    private final CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);

    public List<CurrencyDto> getAllCurrencies() {
        return executeInTransaction(em ->
                em.createQuery("SELECT c FROM Currency c", Currency.class)
                        .getResultList()
                        .stream()
                        .map(currencyMapper::currencyToCurrencyDto)
                        .collect(Collectors.toList())
        );
    }

    public Optional<CurrencyDto> getCurrencyByCode(String code) {
        return executeInTransaction(em ->
                em.createQuery("SELECT c FROM Currency c WHERE c.code = :code", Currency.class)
                        .setParameter("code", code)
                        .getResultList()
                        .stream()
                        .findFirst()
                        .map(currencyMapper::currencyToCurrencyDto)
        );
    }

    public CurrencyDto saveCurrency(CurrencyDto currencyDto) {
        return executeInTransaction(em -> {
            Currency currency = currencyMapper.currencyDtoToCurrency(currencyDto);
            em.persist(currency);
            return currencyMapper.currencyToCurrencyDto(currency);
        });
    }

    private <T> T executeInTransaction(TransactionTask<T> task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = task.execute(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            if (tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
        throw new RuntimeException("Error executing transaction");
    }
}
