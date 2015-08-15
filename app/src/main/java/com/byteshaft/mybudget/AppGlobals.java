package com.byteshaft.mybudget;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    private static final String LOG_TAG = "MyBudget";

    public static Context getContext() {
        return sContext;
    }

    public static String getLogTag(Class aClass) {
        return LOG_TAG+aClass.getName();

    }
}
