package com.github.eletransactionviewer.BacgroundTask;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.github.eletransactionviewer.model.MonthWiseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AT-Praveen on 27/02/18.
 */

public class ParseMonthWiseData extends AsyncTask<Cursor, Void, List<MonthWiseModel>> {
    private static final String TAG = ParseMonthWiseData.class.getSimpleName();

    private String currencySymbol;
    private ParseMonthWiseDataCallback parseMessageCallback;

    public ParseMonthWiseData(ParseMonthWiseDataCallback callback) {
        parseMessageCallback = callback;
    }


    @Override
    protected List<MonthWiseModel> doInBackground(Cursor... cursors) {
        Cursor c = cursors[0];

        List<MonthWiseModel> dataList = new ArrayList<>();
        Currency currency = Currency.getInstance("INR");
        currencySymbol = currency.getSymbol(new Locale("en", "in"));
        Log.e(TAG, currencySymbol);

        while (c.moveToNext()) {
            String senderAddres = c.getString(c.getColumnIndexOrThrow("address"));
            String body = c.getString(c.getColumnIndexOrThrow("body"));

            //Search for Messsages with XX-XXXXXX pattern in address
            Pattern pattern = Pattern.compile("[a-zA-Z0-9]{2}-([a-zA-Z0-9]{6})");
            Matcher matcher = pattern.matcher(senderAddres);

            body = body.toLowerCase().replaceAll(",", "");
            Log.e(TAG, "doInBackground() | " + senderAddres + "|" + body);

            //boolean spentIndex = body.indexOf("spent") > 0 ;
            boolean containCurrencySymbol = body.contains("rs.");
            //Log.e(TAG, "SMS contains containCurrencySymbol: " + containCurrencySymbol);

            if (matcher.find() && ((body.indexOf("spent") > 0 && containCurrencySymbol) || body.contains("txn of inr"))) {
                Log.e(TAG, "First pattern matched");

                MonthWiseModel data = extractData(body);

                if (data != null) {
                    //Log.e(TAG, data.toString());
                    if (dataList.contains(data)) {
                        int index = dataList.indexOf(data);
                        MonthWiseModel data1 = dataList.get(index);
                        data1.setTotalAmount(data1.getTotalAmount() + data.getTotalAmount());
                        data1.setTotalAmountString(currencySymbol + String.valueOf(data1.getTotalAmount().intValue()));
                        dataList.set(index, data1);
                        data = null;
                    } else {
                        data.setBankName(matcher.group(1));
                        dataList.add(data);
                    }
                }
            }

        }
        Collections.sort(dataList);
        return dataList;
    }


    @Override
    protected void onPostExecute(List<MonthWiseModel> monthWiseModels) {
        super.onPostExecute(monthWiseModels);
        //Log.e(TAG, "onPostExecute : list : " + monthWiseModels.size());
        if (parseMessageCallback != null) {
            parseMessageCallback.onPostExecute(monthWiseModels);
        }
    }

    private MonthWiseModel extractData(String msg) {
        String transAmountRegex = ".*?([+-]?\\d*\\.\\d+)(?![-+0-9\\.])";
        String cardNumberRegex = "((?:\\s+(\\d{4})\\s+)|(?:[a-z][a-z]*[0-9]+[a-z0-9]*))";
        //match date & string pattern - on date at date patterns -2018-02-20, 06-nov-17, 29 dec 17
        String transDateRegex = "(?:on\\s)((?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/.](?:[0]?[1-9]|[1][012])[-:\\/.](?:(?:[0-2]?\\d{1})|(?:[3][01]{1}))|(?:([0-9])|(?:[0-2][0-9])|([3][0-1]))(?:\\-*|\\s*)(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(?:\\-*|\\s*)\\d{2})";


        Matcher transAmountMatcher = Pattern.compile(transAmountRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        Matcher cardNumberMatcher = Pattern.compile(cardNumberRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        Matcher transDateMatcher = Pattern.compile(transDateRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        if (transAmountMatcher.find() && cardNumberMatcher.find() && transDateMatcher.find()) {

            Log.e(TAG, transAmountMatcher.group(1) + "|" + cardNumberMatcher.group(1) + "|" + transDateMatcher.group(1));

            Double transAmount = Double.parseDouble(transAmountMatcher.group(1));
            String cardNumber = cardNumberMatcher.group(1);
            cardNumber = cardNumber.trim().replaceAll("x", "");
            cardNumber = "xxxxxxxx" + cardNumber;

            MonthWiseModel data = new MonthWiseModel();
            data.setCardnumber(cardNumber);
            data.setTotalAmount(transAmount);
            data.setTotalAmountString(currencySymbol + String.valueOf(transAmount.intValue()));
            return data;

        }
        return null;
    }


    public interface ParseMonthWiseDataCallback {
        void onPreExecute();
        void onPostExecute(List<MonthWiseModel> dataModelList);
    }
}
