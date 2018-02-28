package com.github.eletransactionviewer.monthlyTransactions;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.eletransactionviewer.BacgroundTask.ParseMonthWiseData;
import com.github.eletransactionviewer.R;
import com.github.eletransactionviewer.model.MonthWiseModel;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthWiseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthWiseFragment extends Fragment implements ParseMonthWiseData.ParseMonthWiseDataCallback {
    private static final String TAG = MonthWiseFragment.class.getSimpleName();

    private RecyclerView mRecyclerView = null;
    private ProgressBar mProgressBar = null;
    private MonthWiseAdaptor mAdapter = null;
    private LinearLayout errorLayoutContainer;

    private FloatingActionButton floatingActionButton;
    private TextView totalMonthAmountTextView;
    private TextView cardsUsed;
    private TextView transactionPeriod;

    private Context mContext;
    private Cursor mCursor;
    private ParseMonthWiseData parseMonthWiseData;
    private String [] month_array = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    int mYear;
    int mMonth;
    int mDay;

    public MonthWiseFragment() {
        // Required empty public constructor
    }

    public static MonthWiseFragment newInstance(String param1, String param2) {
        return new MonthWiseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_month_wise_view, container, false);


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mProgressBar = view.findViewById(R.id.progressBar1);
        errorLayoutContainer = view.findViewById(R.id.error_message_container);

        totalMonthAmountTextView = view.findViewById(R.id.total_month_amount);
        cardsUsed = view.findViewById(R.id.total_card_used);
        transactionPeriod = view.findViewById(R.id.transaction_period);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showDatePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        showProgress();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);

        String period  = month_array[mMonth] + ", " + mYear;
        transactionPeriod.setText(period);
        fetchData(mMonth, mYear);
    }

    DatePickerDialog.OnDateSetListener mDateSetListner = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String period  = month_array[mMonth] + ", " + mYear;
            transactionPeriod.setText(period);

            fetchData(mMonth, mYear);
        }
    };

    private void showDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(mContext, mDateSetListner, mYear, mMonth, mDay);
        final Calendar c = Calendar.getInstance();

        dpd.getDatePicker().setMaxDate(c.getTimeInMillis());

        c.add(Calendar.YEAR, -1);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dpd.setTitle("Select Month and Year");
        dpd.show();
    }

    private void fetchData(int month,  int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_MONTH, 1);

        Long startOfMonth = cal.getTimeInMillis();

        // get start of the next month
        cal.add(Calendar.MONTH, 1);

        Long endOfMonth = cal.getTimeInMillis();

        Log.e(TAG, "Start Of Month: " + startOfMonth + " | end of month: " + endOfMonth);

        ContentResolver cr = mContext.getContentResolver();

        mCursor = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox.BODY}, // Select body text
                Telephony.TextBasedSmsColumns.DATE + " >= ? and " + Telephony.TextBasedSmsColumns.DATE + " < ?",
                new String[] {"" + startOfMonth, "" + endOfMonth},
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        if (mCursor != null && mCursor.getCount() > 0) {
            Log.e(TAG, "fetchData() Total Messages: " + mCursor.getCount());
            mCursor.moveToFirst();

            parseMonthWiseData = new ParseMonthWiseData(this);
            parseMonthWiseData.execute(mCursor);
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
            cardsUsed.setText(String.valueOf(0));
            totalMonthAmountTextView.setText(mContext.getResources().getString(R.string.currency, 0));


        }
    }

    public void showProgress() {
        if (mProgressBar != null && mProgressBar.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(List<MonthWiseModel> dataModelList) {
        Log.e(TAG, "onPostExecute : listt : " + dataModelList.size());
        if (mAdapter == null) {
            mAdapter = new MonthWiseAdaptor();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setDataList(dataModelList);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.setDataList(dataModelList);
        }
        cardsUsed.setText(String.valueOf(dataModelList.size()));
        totalMonthAmountTextView.setText(mContext.getResources().getString(R.string.currency, calculateMonthlyAmount(dataModelList)));


        if ((mProgressBar != null && mProgressBar.getVisibility() != View.GONE) || errorLayoutContainer.getVisibility() != View.GONE) {
            errorLayoutContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private int calculateMonthlyAmount(List<MonthWiseModel> dataModelList) {
        int amount = 0;
        for (MonthWiseModel data : dataModelList) {
            amount += data.getTotalAmount().intValue();
        }
        return amount;
    }
}
