package models;

public enum CurrencyCode {
    INR("Rs."),
    USD("$"),
    EUR("€");

    private final String symbol;

    CurrencyCode(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){ return symbol; }

}
