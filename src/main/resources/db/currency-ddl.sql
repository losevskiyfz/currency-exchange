CREATE TABLE Currencies
(
    ID       INTEGER PRIMARY KEY,
    Code     VARCHAR,
    FullName VARCHAR,
    Sign     VARCHAR
);
CREATE UNIQUE INDEX idx_currency_code ON Currencies (Code);

CREATE TABLE ExchangeRates
(
    ID               INTEGER PRIMARY KEY,
    BaseCurrencyId   INTEGER,
    TargetCurrencyId INTEGER,
    Rate             DECIMAL(10, 6),
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID)
        ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE UNIQUE INDEX idx_currency_pair ON ExchangeRates (BaseCurrencyId, TargetCurrencyId);