package com.github.eletransactionviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.eletransactionviewer.allTransactions.AllTransactionFragment;
import com.github.eletransactionviewer.monthlyTransactions.MonthWiseFragment;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (isPermissionGranted()) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }
    public boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED ) {
                //Log.e(TAG, "Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_SMS
                }, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.e(TAG, "Permission is granted");
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
                //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);


            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MonthWiseFragment.newInstance("", "");
                case 1:
                    return AllTransactionFragment.newInstance();
                default:
                    return MonthWiseFragment.newInstance("", "");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Monthly Transactions";
                case 1:
                    return "All Transactions";
            }
            return super.getPageTitle(position);
        }
    }
}
