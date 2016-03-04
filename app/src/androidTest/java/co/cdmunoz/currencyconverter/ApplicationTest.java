package co.cdmunoz.currencyconverter;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

import co.cdmunoz.currencyconverter.util.Utility;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    private MainActivity mainActivity;

    @Test
    public void validateExchageValue(){
        double usd= 2;
        double eurExchange = 2;
        double expectedResult = 4;

        double result = Utility.getExchangeValue(usd, eurExchange);

        assertEquals("The method getExchangeValue return incorrect result", expectedResult, result);

    }

    @Test
    public void validateFormatExchageValue(){

        double valueExchange = 2.08145;
        double expectedResult = 2.08;

        String result = Utility.formatExchangeValue(valueExchange);

        assertEquals("The method formatExchangeValue return incorrect result", expectedResult, result);

    }

}