package com.github.ele_sms.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        if (isPermissionGranted()) {
            presenter.fetchData();
        }

    }


    public boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED ) {
                Log.e(TAG, "Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_SMS
                }, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e(TAG, "Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                presenter.fetchData();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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
