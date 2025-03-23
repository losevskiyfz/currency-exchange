INSERT INTO Currencies (Code, FullName, Sign)
VALUES
    ('USD', 'US Dollar', '$'),
    ('EUR', 'Euro', '€'),
    ('GBP', 'Pound Sterling', '£'),
    ('JPY', 'Yen', '¥'),
    ('AUD', 'Australian Dollar', 'A$'),
    ('CAD', 'Canadian Dollar', 'C$'),
    ('CHF', 'Swiss Franc', '₣'),
    ('INR', 'Indian Rupee', '₹'),
    ('CNY', 'Yuan Renminbi', '¥'),
    ('MXN', 'Mexican Peso', 'M$');

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES
    (1, 2, 0.85),  -- 1 USD to EUR
    (1, 3, 0.75),  -- 1 USD to GBP
    (1, 4, 110.25), -- 1 USD to JPY
    (1, 5, 1.35),  -- 1 USD to AUD
    (1, 6, 1.25),  -- 1 USD to CAD
    (1, 7, 0.92),  -- 1 USD to CHF
    (1, 8, 74.50), -- 1 USD to INR
    (1, 9, 6.45),  -- 1 USD to CNY
    (1, 10, 20.40), -- 1 USD to MXN
    (2, 1, 1.18),  -- 1 EUR to USD
    (2, 3, 0.88),  -- 1 EUR to GBP
    (2, 4, 130.13), -- 1 EUR to JPY
    (2, 5, 1.58),  -- 1 EUR to AUD
    (2, 6, 1.47),  -- 1 EUR to CAD
    (2, 7, 1.08),  -- 1 EUR to CHF
    (2, 8, 87.60), -- 1 EUR to INR
    (2, 9, 7.59),  -- 1 EUR to CNY
    (2, 10, 24.00); -- 1 EUR to MXN