package com.github.ele_sms.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.ele_sms.MainActivityContract;
import com.github.ele_sms.presenter.MainActivityPresenter;
import com.github.ele_sms.R;
import com.github.ele_sms.model.Data_Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AT-Praveen on 20/02/18.
 */

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView = null;
    private ProgressBar mProgressBar = null;
    private LayoutAdapter mAdapter = null;
    private MainActivityContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.content_progress_bar);

        presenter = new MainActivityPresenter(this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkAndRequestPermissions();
        presenter.fetchData();

    }


    private  boolean checkAndRequestPermissions()
    {
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showProgress() {
        if (mProgressBar != null && mProgressBar.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showData(List<Data_Model> dataModelList) {
        if (mAdapter == null) {
            mAdapter = new LayoutAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setList(dataModelList);
        } else {
            mAdapter.setList(dataModelList);
        }

        if (mProgressBar != null && mProgressBar.getVisibility() != View.GONE) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError() {
        LinearLayout errorLayoutContainer = findViewById(R.id.error_message_container);
        if(errorLayoutContainer != null && errorLayoutContainer.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            errorLayoutContainer.setVisibility(View.VISIBLE);
        }
    }

}
