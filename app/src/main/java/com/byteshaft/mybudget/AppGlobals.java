package com.byteshaft.mybudget;

import android.app.Application;
import android.content.Context;

import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.database.DBHelper;


public class AppGlobals extends Application {

    private static Context sContext;
    private static final String LOG_TAG = "MyBudget";
    private static String sCurrentMonthYear = null;
    public final static String PREFS_NAME = "MyBudgetPrefs";

    public static Context getContext() {
        return sContext;
    }

    public static String getLogTag(Class aClass) {
        return LOG_TAG+aClass.getName();
    }

    public static void setCurrentMonthYear(String value) {
        sCurrentMonthYear = value;
    }

    public static String getsCurrentMonthYear(){
        return sCurrentMonthYear;
    }
}
