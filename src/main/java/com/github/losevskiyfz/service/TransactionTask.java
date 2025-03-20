package com.github.losevskiyfz.service;

import jakarta.persistence.EntityManager;

@FunctionalInterface
public interface TransactionTask<T> {
    T execute(EntityManager em);
}
