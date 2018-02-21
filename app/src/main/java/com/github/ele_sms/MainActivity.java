package com.github.ele_sms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ele_sms.model.Data_Model;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Created by AT-Praveen on 20/02/18.
 */

public class MainActivity extends AppCompatActivity implements ParseMessage.ParseMessageCallback{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView = null;
    private ProgressBar mProgressBar = null;
    private LayoutAdapter mAdapter = null;
    private List<Data_Model> dataModelList = null;
    private  Cursor mCursor = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.content_progress_bar);

        dataModelList = new ArrayList<>();
        mAdapter = new LayoutAdapter(this, dataModelList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        fetchData();
    }

    private void fetchData() {
        Log.e(TAG, "fetchData() ");
        Uri uriSms = Uri.parse("content://sms");

        ContentResolver cr = getContentResolver();
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
            String count = Integer.toString(mCursor.getCount());
            Log.e(TAG, count);
            mCursor.moveToFirst();

            Currency currency = Currency.getInstance( getResources().getConfiguration().locale);
            String symbol = currency.getSymbol();

            ParseMessage parseMessage = new ParseMessage(symbol, this);
            parseMessage.execute(mCursor);
        }

    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(List<Data_Model> data_ModelList) {
        if (mProgressBar != null && mProgressBar.getVisibility() != View.GONE) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        if (data_ModelList == null || data_ModelList.isEmpty()) {
            return;
        }
        if (dataModelList != null) {
            dataModelList.clear();
            dataModelList.addAll(data_ModelList);
            mAdapter.notifyDataSetChanged();
        } else {
            dataModelList = data_ModelList;
            mAdapter.notifyDataSetChanged();
        }

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }
}
