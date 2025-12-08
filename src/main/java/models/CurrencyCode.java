package models;

public enum CurrencyCode {
    ILS("₪"),
    USD("$"),
    EUR("€");

    private final String symbol;

    CurrencyCode(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){ return symbol; }

}
