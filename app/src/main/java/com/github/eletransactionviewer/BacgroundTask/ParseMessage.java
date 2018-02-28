package com.github.eletransactionviewer.BacgroundTask;

import android.database.Cursor;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import com.github.eletransactionviewer.model.Data_Model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AT-Praveen on 21/02/18.
 */

public class ParseMessage extends AsyncTask<Cursor, Void, List<Data_Model>> {
    private static final String TAG = ParseMessage.class.getSimpleName();
    private ParseMessageCallback parseMessageCallback;


    public ParseMessage(ParseMessageCallback parseMessageCallback) {
        this.parseMessageCallback = parseMessageCallback;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (parseMessageCallback != null) {
            parseMessageCallback.onPreExecute();
        }
    }

    @Override
    protected List<Data_Model> doInBackground(Cursor... cursors) {
        Cursor c = cursors[0];
        List<Data_Model> data_modelList = new ArrayList<>();
        Currency currency = Currency.getInstance("INR");
        String currencySymbol = currency.getSymbol(new Locale("en", "in"));


        while (c.moveToNext()) {
            String senderAddres = c.getString(c.getColumnIndexOrThrow("address"));
            String msgDate = c.getString(c.getColumnIndexOrThrow("date"));
            String body = c.getString(c.getColumnIndexOrThrow("body"));


            //Search for Messsages with XX-XXXXXX pattern in address
            Pattern pattern = Pattern.compile("[a-zA-Z0-9]{2}-([a-zA-Z0-9]{6})");
            Matcher matcher = pattern.matcher(senderAddres);

            body = body.toLowerCase().replaceAll(",", "");

            if (matcher.find() && body.indexOf("spent") > 0 && body.contains(currencySymbol.toLowerCase())) {
                //Log.e(TAG, body);
                Data_Model dataModel = parseMessage(body);
                if (dataModel != null) {
                    msgDate = getDate(msgDate);
                    dataModel.setBankName(matcher.group(1));
                    dataModel.setSmsDate(msgDate);
                    data_modelList.add(dataModel);

                }
            }
        }
        return data_modelList;
    }




    private Data_Model parseMessage(String msg) {
        String transAmountRegex = ".*?([+-]?\\d*\\.\\d+)(?![-+0-9\\.])";
        String cardNumberRegex = "((?:\\s+(\\d{4})\\s+)|(?:[a-z][a-z]*[0-9]+[a-z0-9]*))";
        //match date & string pattern - on date at date patterns -2018-02-20, 06-nov-17, 29 dec 17
        String transDateRegex = "(?:on\\s)((?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))[-:\\/.](?:[0]?[1-9]|[1][012])[-:\\/.](?:(?:[0-2]?\\d{1})|(?:[3][01]{1}))|(?:([0-9])|(?:[0-2][0-9])|([3][0-1]))(?:\\-*|\\s*)(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(?:\\-*|\\s*)\\d{2})";


        Matcher transAmountMatcher = Pattern.compile(transAmountRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        Matcher cardNumberMatcher = Pattern.compile(cardNumberRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        Matcher transDateMatcher = Pattern.compile(transDateRegex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(msg);
        if (transAmountMatcher.find() && cardNumberMatcher.find() && transDateMatcher.find())
        {

            Log.e(TAG, transAmountMatcher.group(1) + "|" + cardNumberMatcher.group(1) + "|" + transDateMatcher.group(1));
            String transAmount = NumberFormat.getCurrencyInstance(new Locale("en", "in"))
                    .format(Double.parseDouble(transAmountMatcher.group(1)));
            String transDate = transDateMatcher.group(1);
            String cardNumber = cardNumberMatcher.group(1);

            //transAmount = transAmount;
            //transDate = "Spent On: " + transDate;
            cardNumber = cardNumber.trim().replaceAll("x", "");
            cardNumber = "xxxxxxxx" + cardNumber;

            Data_Model data = new Data_Model();
            data.setTransAmountString(transAmount);
            data.setTransAmount(Double.parseDouble(transAmountMatcher.group(1)));
            data.setTransDate(transDate);
            data.setCardNumber(cardNumber);

            return data;
        }
        return null;
    }

    private String getDate(String timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
    }

    @Override
    protected void onPostExecute(List<Data_Model> data_models) {
        super.onPostExecute(data_models);
        //Log.e(TAG, "onPostExecute : " + "size:" + data_models.size());
        if (parseMessageCallback != null) {
            parseMessageCallback.onPostExecute(data_models);
        }



    }

    public interface ParseMessageCallback {
        void onPreExecute();
        void onPostExecute(List<Data_Model> dataModelList);
    }
}
