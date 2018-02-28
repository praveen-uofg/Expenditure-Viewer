package com.github.eletransactionviewer.allTransactions;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.eletransactionviewer.BacgroundTask.ParseMessage;
import com.github.eletransactionviewer.R;
import com.github.eletransactionviewer.model.Data_Model;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllTransactionFragment extends Fragment implements ParseMessage.ParseMessageCallback{

    private static final String TAG = AllTransactionFragment.class.getSimpleName();

    private RecyclerView mRecyclerView = null;
    private ProgressBar mProgressBar = null;
    private LinearLayout errorLayoutContainer;

    private AllTransactionAdapter mAdapter;
    private ParseMessage parseMessage;

    private Context mContext;
    private Cursor mCursor;


    public AllTransactionFragment() {
        // Required empty public constructor
    }
    public static AllTransactionFragment newInstance() {
        return new AllTransactionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transaction, container, false);


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mProgressBar = view.findViewById(R.id.progressBar1);
        errorLayoutContainer = view.findViewById(R.id.error_message_container);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        showProgress();
        fetchData();
    }


    void fetchData() {
        Log.e(TAG, "fetchData()");
        ContentResolver cr = mContext.getContentResolver();
        mCursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox.BODY}, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        if (mCursor != null && mCursor.getCount() > 0) {
            Log.e(TAG, "fetchData() Total Messages: "  +mCursor.getCount());
            mCursor.moveToFirst();

            parseMessage = new ParseMessage(this);
            parseMessage.execute(mCursor);
        } else {
            showError();
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
        }
    }


    private void showError() {
        if(errorLayoutContainer != null && errorLayoutContainer.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            errorLayoutContainer.setVisibility(View.VISIBLE);
        }
    }

    public void showProgress() {
        if (mProgressBar != null && mProgressBar.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(List<Data_Model> dataModelList) {
        Log.e(TAG, "onPostExecute : list : " + dataModelList.size());
        if (mAdapter == null) {
            mAdapter = new AllTransactionAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setDataList(dataModelList);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.setDataList(dataModelList);
        }

        if (mProgressBar != null && mProgressBar.getVisibility() != View.GONE) {
            errorLayoutContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
