package co.cdmunoz.currencyconverter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.cdmunoz.currencyconverter.model.CurrencyExchange;
import co.cdmunoz.currencyconverter.util.Utility;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    final String FIO_GBP = "GBP";
    final String FIO_EUR = "EUR";
    final String FIO_JPY = "JPY";
    final String FIO_BRL = "BRL";

    @Bind(R.id.dollarEditText) EditText dollarEditText;
    @Bind(R.id.convertButton) Button convertButton;
    @Bind(R.id.gbpTextView) TextView gbpTextView;
    @Bind(R.id.eurTextView) TextView eurTextView;
    @Bind(R.id.jpyTextView) TextView jpyTextView;
    @Bind(R.id.brlTextView) TextView brlTextView;
    @Bind(R.id.gbpValTextView) TextView gbpValTextView;
    @Bind(R.id.eurValTextView) TextView eurValTextView;
    @Bind(R.id.jpyValTextView) TextView jpyValTextView;
    @Bind(R.id.brlValTextView) TextView brlValTextView;
    @Bind(R.id.chart) BarChart chart;

    double dUSDVal=0;

    private List<CurrencyExchange> mCurrencies = new ArrayList<CurrencyExchange>();
    private ArrayList<CurrencyExchange> currenciesList = new ArrayList<CurrencyExchange>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //binding the views using butterknife library -- app's build.gradle com.jakewharton:butterknife:7.0.1
        ButterKnife.bind(this);
        //hide no form views
        hideViews();
        //performs search using ENTER Key
        dollarEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    if (!TextUtils.isEmpty(dollarEditText.getText()) && Double.parseDouble(dollarEditText.getText().toString()) > 0) {
                        dUSDVal = Double.valueOf(dollarEditText.getText().toString());
                        searchExchange();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_usd_value), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
        //performs search
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dollarEditText.getText()) && Double.parseDouble(dollarEditText.getText().toString()) > 0) {
                    dUSDVal = Double.valueOf(dollarEditText.getText().toString());
                    searchExchange();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_usd_value), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Sets app icon in the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_action);

        //restoring state after a deveice orientation change
        if(null != savedInstanceState){
            currenciesList = (ArrayList<CurrencyExchange>) savedInstanceState.getSerializable("currenciesList");
            populateExchangeInfoAndDrawChart(currenciesList);
            mCurrencies = currenciesList;
        }else{
            currenciesList = new ArrayList<CurrencyExchange>();
        }
    }

    /**
     * Hides every view in the activity but the form using Gone instead of Invisible
     */
    private void hideViews(){
        gbpTextView.setVisibility(View.GONE);
        eurTextView.setVisibility(View.GONE);
        jpyTextView.setVisibility(View.GONE);
        brlTextView.setVisibility(View.GONE);
        gbpValTextView.setVisibility(View.GONE);
        eurValTextView.setVisibility(View.GONE);
        jpyValTextView.setVisibility(View.GONE);
        brlValTextView.setVisibility(View.GONE);
        chart.setVisibility(View.GONE);
    }

    /**
     * Shows every hidden view in the activity
     */
    private void showViews(){
        gbpTextView.setVisibility(View.VISIBLE);
        eurTextView.setVisibility(View.VISIBLE);
        jpyTextView.setVisibility(View.VISIBLE);
        brlTextView.setVisibility(View.VISIBLE);
        gbpValTextView.setVisibility(View.VISIBLE);
        eurValTextView.setVisibility(View.VISIBLE);
        jpyValTextView.setVisibility(View.VISIBLE);
        brlValTextView.setVisibility(View.VISIBLE);
        chart.setVisibility(View.VISIBLE);
    }

    // Check all connectivities whether available or not
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Using an asynctask search exchanges
     */
    private void searchExchange(){
        //hides keyboard after convert button is pressed
        dollarEditText.clearFocus();
        InputMethodManager in = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(dollarEditText.getWindowToken(), 0);

        if (isNetworkAvailable()) {
            //querying the search
            FetchExchangeTask artistsTask = new FetchExchangeTask(this);
            artistsTask.execute();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currenciesList = new ArrayList<CurrencyExchange>(mCurrencies);
        outState.putSerializable("currenciesList", currenciesList);
    }

    /**
     * Inner class to query the exchanges through fixer.io's API
     */
    public class FetchExchangeTask extends AsyncTask<Void, Void, List<CurrencyExchange>>{

        private final String LOG_TAG = FetchExchangeTask.class.getSimpleName();
        ProgressDialog progressDialog;
        Context context;

        public FetchExchangeTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.progress_dialog_msg));
            progressDialog.show();
        }

        @Override
        protected List<CurrencyExchange> doInBackground(Void... params) {

            final String EXCHANGE_BASE_URL = "http://api.fixer.io/latest?base=USD";

            List<CurrencyExchange> currencies = null;//returned currencies

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String exchangeJsonStr = null;

            try{
                //build the URI with API URL
                Uri builtUri = Uri.parse(EXCHANGE_BASE_URL).buildUpon().build();
                URL url = new URL(builtUri.toString());
                // Create the request to fixer.io, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                exchangeJsonStr = buffer.toString();

                Log.d(LOG_TAG, "exchangeJsonStr: "+exchangeJsonStr);

                try {
                    currencies = getExchangeDataFromJson(exchangeJsonStr);
                }catch (JSONException e){
                    Log.e(LOG_TAG, "Error getting JSON answer: ", e);
                    return null;
                }

            }catch(IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the exchange data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return currencies;
        }

        /**
         * Gets the data from the result after querying fixer.io
         * @param exchangeJsonStr String with API result
         * @return A list of CurrencyExchange objects with converison info
         * @throws JSONException
         */
        private List<CurrencyExchange> getExchangeDataFromJson(String exchangeJsonStr) throws JSONException{

            final String FIO_RATES = "rates";

            double valueGBP;
            double valueEUR;
            double valueJPY;
            double valueBRL;

            List<CurrencyExchange> currencies = new ArrayList<CurrencyExchange>();

            try{
                JSONObject exchangeJson = new JSONObject(exchangeJsonStr);
                JSONObject exchangeJsonRates = exchangeJson.getJSONObject(FIO_RATES);

                valueGBP = exchangeJsonRates.getDouble(FIO_GBP);
                valueEUR = exchangeJsonRates.getDouble(FIO_EUR);
                valueJPY = exchangeJsonRates.getDouble(FIO_JPY);
                valueBRL = exchangeJsonRates.getDouble(FIO_BRL);

                CurrencyExchange currencyExchangeGBP = new CurrencyExchange(FIO_GBP, Utility.getExchangeValue(dUSDVal, valueGBP));
                currencies.add(currencyExchangeGBP);
                CurrencyExchange currencyExchangeEUR = new CurrencyExchange(FIO_EUR, Utility.getExchangeValue(dUSDVal, valueEUR));
                currencies.add(currencyExchangeEUR);
                CurrencyExchange currencyExchangeJPY = new CurrencyExchange(FIO_JPY, Utility.getExchangeValue(dUSDVal, valueJPY));
                currencies.add(currencyExchangeJPY);
                CurrencyExchange currencyExchangeBRL = new CurrencyExchange(FIO_BRL, Utility.getExchangeValue(dUSDVal, valueBRL));
                currencies.add(currencyExchangeBRL);

            } catch (JSONException jsone){
                Log.e(LOG_TAG, jsone.getMessage(), jsone);
                jsone.printStackTrace();
            }
            return currencies;
        }

        @Override
        protected void onPostExecute(List<CurrencyExchange> result) {
            progressDialog.dismiss();//hides the dialog
            if(result != null && !result.isEmpty()){
                populateExchangeInfoAndDrawChart(result);
                mCurrencies = result;
            }
        }
    }

    /**
     * Populates exchange information
     * @param currencies    exchange information
     */
    private void populateExchangeInfoAndDrawChart(List<CurrencyExchange> currencies){
        if(null != currencies && !currencies.isEmpty()){
            //format every conversion to a 2 decimals value
            gbpValTextView.setText(Utility.formatExchangeValue(currencies.get(0).getCurrencyValue()));//GBP
            eurValTextView.setText(Utility.formatExchangeValue(currencies.get(1).getCurrencyValue()));//EUR
            jpyValTextView.setText(Utility.formatExchangeValue(currencies.get(2).getCurrencyValue()));//JPY
            brlValTextView.setText(Utility.formatExchangeValue(currencies.get(3).getCurrencyValue()));//BRL
            //shows hidden views
            showViews();
            //draw chart using MPAndroidChart library -- app's build.gradle com.github.PhilJay:MPAndroidChart:v2.0.9
            BarData data = new BarData(getXAxisValues(), getDataSet(currencies));
            chart.setData(data);
            chart.setDescription(getResources().getString(R.string.chart_description));
            chart.animateXY(1500, 1500);
            chart.invalidate();//to make the bar chart reflect this latest data
        }
    }

    /**
     * Set the string that will be used in X axis of the chart
     * @return X axis values in a list
     */
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add(FIO_GBP);
        xAxis.add(FIO_EUR);
        xAxis.add(FIO_JPY);
        xAxis.add(FIO_BRL);
        return xAxis;
    }

    /**
     * Sets the information of the chart
     * @return information to be displayed in the chart in a list
     */
    private ArrayList<BarDataSet> getDataSet(List<CurrencyExchange> currencies) {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry((float)currencies.get(0).getCurrencyValue(), 0); //GBP
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry((float)currencies.get(1).getCurrencyValue(), 1); //EUR
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry((float)currencies.get(2).getCurrencyValue(), 2); //JPY
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry((float)currencies.get(3).getCurrencyValue(), 3); //BRL
        valueSet1.add(v1e4);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, getResources().getString(R.string.chart_title));
        barDataSet1.setColor(Color.rgb(0, 155, 0));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }
}
