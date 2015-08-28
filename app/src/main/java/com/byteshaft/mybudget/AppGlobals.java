package com.byteshaft.mybudget;

import android.app.Application;
import android.content.Context;


public class AppGlobals extends Application {

    private static Context sContext;
    private static final String LOG_TAG = "MyBudget";
    private static String sCurrentMonthYear = null;
    public final static String PREFS_NAME = "MyBudgetPrefs";
    private static boolean sDatePickerState = false;
    private static String sDatePickerValues;
    private static boolean sDpCurrentMonthExist;

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

    public static void setDatePickerState(boolean value) {
        sDatePickerState = value;
    }

    public static boolean getDatePickerState() {
        return sDatePickerState;
    }

    public static void setsDatePickerValues(String values) {
        sDatePickerValues = values;
    }

    public static String getDatePickerValues() {
        return sDatePickerValues;
    }

    public static void setsDpCurrentMonthExist(boolean value) {
        sDpCurrentMonthExist = value;
    }

    public static boolean getDpCurrentMonthExist() {
        return sDpCurrentMonthExist;
    }
}
