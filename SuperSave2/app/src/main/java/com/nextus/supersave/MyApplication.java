package com.nextus.supersave;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nextus.supersave.db.CustomSQLiteHelper;

/**
 * Created by chosw on 2016-08-03.
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    private static CustomSQLiteHelper helper;
    public float total_kwh = 0;
    public SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        helper = new CustomSQLiteHelper( this, "ELECTRO_DATA.db", null, 1);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }


    public CustomSQLiteHelper getHelper()
    {
        return helper;
    }

    public float getTotal_kwh()
    {
        return total_kwh;
    }

    public void setTotal_kwh(float kwh)
    {
        total_kwh = kwh;
    }

}
