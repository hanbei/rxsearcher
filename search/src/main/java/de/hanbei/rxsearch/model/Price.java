package de.hanbei.rxsearch.model;

import com.google.common.base.Preconditions;

import java.util.Currency;

import static com.google.common.base.Preconditions.checkNotNull;

public class Price {

    public static final Price ZERO = new Price(0.0, "USD");

    private final Currency currency;
    private final double amount;

    public Price(double amount, String currency) {
        this(amount, Currency.getInstance(currency));
    }

    public Price(double amount, Currency currency) {
        this.currency = checkNotNull(currency, "Currency is not allowed to be null");
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }
}
