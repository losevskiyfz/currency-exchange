package com.github.losevskiyfz.bootstrap;

import com.github.losevskiyfz.cdi.ApplicationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.logging.Logger;

public class DataLoader {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final EntityManagerFactory emf = context.resolve(EntityManagerFactory.class);
    private static final Logger logger
            = Logger.getLogger(DataLoader.class.getName());

    public void init() {
        createDdl();
        addInitialData();
    }

    private void createDdl() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery(SqlQueries.CREATE_CURRENCY_TABLE).executeUpdate();
            em.createNativeQuery(SqlQueries.CREATE_EXCHANGE_RATE_TABLE).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void addInitialData() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createNativeQuery("INSERT INTO currency (code, full_name, sign) VALUES (?, ?, ?)")
                    .setParameter(1, "USD")
                    .setParameter(2, "United States Dollar")
                    .setParameter(3, "$")
                    .executeUpdate();

            em.createNativeQuery("INSERT INTO currency (code, full_name, sign) VALUES (?, ?, ?)")
                    .setParameter(1, "EUR")
                    .setParameter(2, "Euro")
                    .setParameter(3, "€")
                    .executeUpdate();

            em.createNativeQuery("INSERT INTO currency (code, full_name, sign) VALUES (?, ?, ?)")
                    .setParameter(1, "JPY")
                    .setParameter(2, "Japanese Yen")
                    .setParameter(3, "¥")
                    .executeUpdate();

            em.createNativeQuery("INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)")
                    .setParameter(1, getCurrencyId(em, "USD"))
                    .setParameter(2, getCurrencyId(em, "EUR"))
                    .setParameter(3, 1.0)
                    .executeUpdate();

            em.createNativeQuery("INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)")
                    .setParameter(1, getCurrencyId(em, "USD"))
                    .setParameter(2, getCurrencyId(em, "JPY"))
                    .setParameter(3, 130.0)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.severe("Error inserting initial data: " + e.getMessage());
        }
    }

    private int getCurrencyId(EntityManager em, String currencyCode) {
        return ((Number) em.createNativeQuery("SELECT id FROM currency WHERE code = ?")
                .setParameter(1, currencyCode)
                .getSingleResult()).intValue();
    }

    private static class SqlQueries {
        private static final String CREATE_CURRENCY_TABLE = """
                    CREATE TABLE IF NOT EXISTS currency (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        code VARCHAR(255),
                        full_name VARCHAR(255),
                        sign VARCHAR(10)
                    );
                """;
        private static final String CREATE_EXCHANGE_RATE_TABLE = """
                    CREATE TABLE IF NOT EXISTS exchange_rate (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        base_currency_id INTEGER,
                        target_currency_id INTEGER,
                        rate DECIMAL(6),
                        FOREIGN KEY (base_currency_id) REFERENCES currency(id),
                        FOREIGN KEY (target_currency_id) REFERENCES currency(id)
                    );
                """;
    }
}
