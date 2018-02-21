package com.github.ele_sms.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.github.ele_sms.MainActivityContract;
import com.github.ele_sms.model.Data_Model;

import java.util.Currency;
import java.util.List;

/**
 * Created by AT-Praveen on 21/02/18.
 */

public class MainActivityPresenter implements MainActivityContract.Presenter, ParseMessage.ParseMessageCallback {
    private static final String TAG = MainActivityPresenter.class.getSimpleName();
    private MainActivityContract.View mView;
    private Context mContext;
    private Cursor mCursor;
    private ParseMessage parseMessage;

    public MainActivityPresenter(MainActivityContract.View view, Context context) {
        this.mContext = context;
        this.mView = view;
    }


    @Override
    public void fetchData() {
        ContentResolver cr = mContext.getContentResolver();
        Uri uriSms = Uri.parse("content://sms");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mCursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                    new String[] { Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox.BODY}, // Select body text
                    null,
                    null,
                    Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        } else {
            mCursor = cr.query(uriSms, new String[] { "_id", "address", "date", "body"}, null, null, null); // Default sort order
        }

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();

            Currency currency = Currency.getInstance(mContext.getResources().getConfiguration().locale);
            String symbol = currency.getSymbol();

            parseMessage = new ParseMessage(symbol, this);
            parseMessage.execute(mCursor);
        } else {
            mView.showError();
            closeCursor();
        }
    }

    private void closeCursor() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    @Override
    public void onDestroy() {
        if (parseMessage != null && !parseMessage.isCancelled()) {
            parseMessage.cancel(true);
        }
        closeCursor();
    }

    @Override
    public void onPreExecute() {
        mView.showProgress();
    }

    @Override
    public void onPostExecute(List<Data_Model> dataModelList) {
        closeCursor();
        if (dataModelList != null && !dataModelList.isEmpty()) {
            mView.showData(dataModelList);
        } else {
            mView.showError();
        }
    }
}
