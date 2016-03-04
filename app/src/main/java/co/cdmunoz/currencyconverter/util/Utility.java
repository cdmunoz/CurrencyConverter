package co.cdmunoz.currencyconverter.util;

/**
 * Created by cdmunoz on 06/12/2015.
 */
public class Utility {

    public static final String EXCHANGE_FORMAT = "%.2f";

    /**
     *
     * Get the exchange value from a factor and a currency value
     *
     * @param dollarValue   Value in dollars
     * @param currencyValue Value in a different currency
     * @return  Exchange value
     */
    public static double getExchangeValue(double dollarValue, double currencyValue){
        return dollarValue * currencyValue;
    }

    /**
     *
     * Formats a double value with 2 decimal format
     *
     * @param currencyValue Value to be formatted
     * @return  value formatted
     */
    public static String formatExchangeValue(double currencyValue){
        return String.format(EXCHANGE_FORMAT, currencyValue);
    }
}
