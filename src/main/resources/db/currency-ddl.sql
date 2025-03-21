CREATE TABLE Currencies
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Code     VARCHAR,
    FullName VARCHAR,
    Sign     VARCHAR
);
CREATE UNIQUE INDEX idx_currency_code ON Currencies (Code);

CREATE TABLE ExchangeRates
(
    ID               INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId   INTEGER,
    TargetCurrencyId INTEGER,
    Rate             DDECIMAL(10, 6)
);
CREATE UNIQUE INDEX idx_currency_pair ON ExchangeRates (BaseCurrencyId, TargetCurrencyId);