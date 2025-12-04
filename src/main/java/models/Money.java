package models;

import java.util.Objects;

public class Money {
    private final long amount;
    private final CurrencyCode currency;

    public Money(long amount, CurrencyCode currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount(){ return amount; }
    public CurrencyCode getCurrency(){ return currency; }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.getCurrency());
    }

    @Override
    public int hashCode(){
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString(){
        return String.format("%s%.2f", currency.getSymbol(), amount / 100.0);
    }
}
