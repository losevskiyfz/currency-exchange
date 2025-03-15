package com.github.losevskiyfz.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.logging.Logger;

@Stateless
public class CurrenciesService {
    private static final Logger logger
            = Logger.getLogger(CurrenciesService.class.getName());

    @PersistenceContext
    private EntityManager em;
}