package com.nextus.supersave;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by chosw on 2016-07-01.
 */
public class Calculator {

    private float kWH = 0;
    private float exKWH = 0;
    private Context context;
    private int level = 0;
    private int exLevel = 0;
    private int[] level_basic_money = {410, 910, 1600, 3850, 7300, 12940};
    private double[] level_kwh_money = {60.7, 125.9, 187.9, 280.6, 417.7, 709.5};
    private int total_money = 0;
    private int exTotal_money = 0;

    private static Calculator calculator;

    public static Calculator getInstance()
    {
        if( calculator == null )
            calculator = new Calculator();
        return calculator;
    }

    public void setData(float kWH, Context mContext)
    {
        this.kWH = kWH;
        context = mContext;

        level_setting_new(kWH);
        calculatig_money();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.e("용도",""+pref.getString("list_preference_02", "test"));
    }

    public void setExpectData(float kWH)
    {
        this.exKWH = kWH;

        expectingLevel(kWH);
        expectMoney();

        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private Calculator()
    {

    }

    private void level_setting(float kWH)
    {
        if( kWH <= 100 ) level = 0;
        else if ( kWH > 100 && kWH <= 200 ) level = 1;
        else if ( kWH > 200 && kWH <= 300 ) level = 2;
        else if ( kWH > 300 && kWH <= 400 ) level = 3;
        else if ( kWH > 400 && kWH <= 500 ) level = 4;
        else if ( kWH > 500) level = 5;
    }

    private void level_setting_new(float kWH)
    {
        if( kWH <= 150 ) level = 0;
        else if ( kWH > 150 && kWH <= 250 ) level = 1;
        else if ( kWH > 250 && kWH <= 350 ) level = 2;
        else if ( kWH > 350 && kWH <= 450 ) level = 3;
        else if ( kWH > 450 && kWH <= 550 ) level = 4;
        else if ( kWH > 550 ) level = 5;
    }

    private void expectingLevel(float kWH)
    {
        if( kWH <= 150 ) exLevel = 0;
        else if ( kWH > 150 && kWH <= 250 ) exLevel = 1;
        else if ( kWH > 250 && kWH <= 350 ) exLevel = 2;
        else if ( kWH > 350 && kWH <= 450 ) exLevel = 3;
        else if ( kWH > 450 && kWH <= 550 ) exLevel = 4;
        else if ( kWH > 550 ) exLevel = 5;
    }

    private void calculatig_money()
    {
        int kwh_money = 0;
        int additional_tax = 0;
        int fund = 0;

        for(int i=0; i<level; i++)
        {
            kwh_money += level_kwh_money[i] * 100;
        }

        kwh_money += (kWH - 100*level) * level_kwh_money[level];

        Log.e("기본요금 + 전력량요금", ""+level_basic_money[level] +"+"+kwh_money);
        total_money = level_basic_money[level] + kwh_money;

        additional_tax = (int)(total_money * 0.1);
        fund = (int)(total_money * 0.037);
        fund = (int)(fund*0.1)*10;

        Log.e("부가세 + 기금", ""+additional_tax +"+"+fund);
        total_money += additional_tax + fund;

        total_money = (int)(total_money*0.1)*10;
    }

    private void expectMoney()
    {
        int kwh_money = 0;
        int additional_tax = 0;
        int fund = 0;

        for(int i=0; i<exLevel; i++)
        {
            kwh_money += level_kwh_money[i] * 100;
        }

        kwh_money += (exKWH - 100*exLevel) * level_kwh_money[exLevel];

        Log.e("기본요금 + 전력량요금", ""+level_basic_money[exLevel] +"+"+kwh_money);
        exTotal_money = level_basic_money[exLevel] + kwh_money;

        additional_tax = (int)(exTotal_money * 0.1);
        fund = (int)(exTotal_money * 0.037);
        fund = (int)(fund*0.1)*10;

        Log.e("부가세 + 기금", ""+additional_tax +"+"+fund);
        exTotal_money += additional_tax + fund;

        exTotal_money = (int)(exTotal_money*0.1)*10;
    }

    public int getTotal_money()
    {
        return total_money;
    }

    public int getExTotal_money() { return exTotal_money; }

}
