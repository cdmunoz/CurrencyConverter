package co.cdmunoz.currencyconverter.model;

import java.io.Serializable;

/**
 * Created by cdmunoz on 02/12/2015.
 * Class that encapsulates currency exchange info (name and value)
 */
public class CurrencyExchange implements /*Parcelable,*/ Serializable {

    private static final long serialVersionUID = -5563597169299191070L;

    private String currencyName;
    private double currencyValue;

    public CurrencyExchange(){

    }

    public CurrencyExchange(String currencyName, double currencyValue) {
        this.currencyName = currencyName;
        this.currencyValue = currencyValue;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getCurrencyValue() {
        return currencyValue;
    }

    public void setCurrencyValue(double currencyValue) {
        this.currencyValue = currencyValue;
    }
/*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString("currencyName", currencyName);
        bundle.putString("currencyValue", String.valueOf(currencyValue));

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }*/

    /**
     * Creator required for class implementing the parcelable interface.
     */
    /*public static final Parcelable.Creator<CurrencyExchange> CREATOR = new Creator<CurrencyExchange>() {

        @Override
        public CurrencyExchange createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate an artist using values from the bundle
            return new CurrencyExchange(bundle.getString("currencyName"),
                    bundle.getDouble("currencyValue"));
        }

        @Override
        public CurrencyExchange[] newArray(int size) {
            return new CurrencyExchange[size];
        }

    };*/
}
