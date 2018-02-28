package com.github.eletransactionviewer;

import android.app.Application;

import com.bugfender.sdk.Bugfender;

/**
 * Created by AT-Praveen on 28/02/18.
 */

public class TransactionViewerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugfender.init(this, "OhD5oABFnT5e3LBMlEaoKasfHRwjQ7GK", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableCrashReporting();
    }
}
