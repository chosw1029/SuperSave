package com.nextus.supersave.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nextus.supersave.ListStructure;
import com.nextus.supersave.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chosw on 2016-06-27.
 */
public class CustomSQLiteHelper extends SQLiteOpenHelper {

    public CustomSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE kwhdata (_id INTEGER PRIMARY KEY AUTOINCREMENT, month INTEGER, date INTEGER, kwh INTEGER);";
        db.execSQL(sql);
        Log.d("DataBase", "Create success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void drop()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table mytable");
        db.close();
    }

    public void delete(int month, int date, float kwh) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "delete from kwhdata where month="+month+" and date="+date+" and kwh="+kwh;
        db.execSQL(query);
        db.close();
    }

    public ArrayList<ListStructure> getData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        ArrayList<ListStructure> list = new ArrayList<ListStructure>();

        Cursor cursor = db.rawQuery("select * from kwhdata", null);
        while(cursor.moveToNext()) {
            ListStructure temp = new ListStructure();
            temp.setMonth(cursor.getInt(1));
            temp.setDate(cursor.getInt(2));
            temp.setKwh(cursor.getInt(3));

            str += cursor.getInt(0)
                    + " : id / "
                    + cursor.getInt(1)
                    + " : month / "
                    + cursor.getInt(2)
                    + " : date "
                    + cursor.getInt(3)
                    + " : kwh "
                    + "\n";

            list.add(temp);
        }
        db.close();
        return list;
    }

    public ArrayList<Float> monthly_data(int month) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        ArrayList<Float> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from kwhdata where month="+month, null); // 이번달 데이터 취합
        while(cursor.moveToNext()) {
            list.add(cursor.getFloat(3));

            str += cursor.getFloat(0)
                    + " : id / "
                    + cursor.getFloat(1)
                    + " : month / "
                    + cursor.getFloat(2)
                    + " : date "
                    + cursor.getFloat(3)
                    + " : kwh "
                    + "\n";
        }

        db.close();
        return list;
    }

    class DataForm
    {
        int month;
        int date;
        float kwh;

        public DataForm(int month, int date, float kwh) {
            this.month = month;
            this.date = date;
            this.kwh = kwh;
        }
    }

    public ArrayList<DataForm> expect(int month, int date)
    {
        ArrayList<DataForm> data = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        ArrayList<Float> list = new ArrayList<>();
        int resetDate = Integer.parseInt(MyApplication.mInstance.preferences.getString("resetDate", "15"));

        if(date <= resetDate)  // 검침일 이전
        {
            int before_month = month-1;
            //이전달 데이터 가져오기
            Cursor cursor = db.rawQuery("select * from kwhdata where month="+before_month+" and date >="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                //list.add(cursor.getFloat(3));
                data.add(new DataForm(cursor.getInt(1), cursor.getInt(2), cursor.getFloat(3)));
                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }

            //이번달 데이터 가져오기
            cursor = db.rawQuery("select * from kwhdata where month="+month+" and date <="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                //list.add(cursor.getFloat(3));
                data.add(new DataForm(cursor.getInt(1), cursor.getInt(2), cursor.getFloat(3)));
                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }
        }
        else  // 검침일 이후
        {
            //이번달 데이터 가져오기
            Cursor cursor = db.rawQuery("select * from kwhdata where month="+month+" and date >="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                //list.add(cursor.getFloat(3));
                data.add(new DataForm(cursor.getInt(1), cursor.getInt(2), cursor.getFloat(3)));
                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }

            //다음달 데이터 가져오기
            int after_month = month+1;
            cursor = db.rawQuery("select * from kwhdata where month="+after_month+" and date <="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                //list.add(cursor.getFloat(3));
                data.add(new DataForm(cursor.getInt(1), cursor.getInt(2), cursor.getFloat(3)));
                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }
        }

        int last_before = data.get(data.size()-2).date;
        int last_date = data.get(data.size()-1).date;

        float average = (data.get(data.size()-1).kwh - data.get(data.size()-2).kwh)/2;
        float last_kwh = data.get(data.size()-1).kwh;

        int level = 0;

        if( last_kwh <= 150 ) { level = 1; }
        else if ( last_kwh > 150 && last_kwh <= 250 ) { level = 2; }
        else if ( last_kwh > 250 && last_kwh <= 350 ) { level = 3; }
        else if ( last_kwh > 350 && last_kwh <= 450 ) { level = 4; }
        else if ( last_kwh > 450 && last_kwh <= 550 ) { level = 5; }
        else if ( last_kwh > 550 && last_kwh <= 650 ) { level = 6; }





        return data;
    }

    public ArrayList<Float> detail_data(int month, int date) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        ArrayList<Float> list = new ArrayList<>();
        int resetDate = Integer.parseInt(MyApplication.mInstance.preferences.getString("resetDate", "15"));

        if(date <= resetDate)  // 검침일 이전
        {
            int before_month = month-1;
            //이전달 데이터 가져오기
            Cursor cursor = db.rawQuery("select * from kwhdata where month="+before_month+" and date >="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                list.add(cursor.getFloat(3));

                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }

            //이번달 데이터 가져오기
            cursor = db.rawQuery("select * from kwhdata where month="+month+" and date <="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                list.add(cursor.getFloat(3));

                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }
        }
        else  // 검침일 이후
        {
            //이번달 데이터 가져오기
            Cursor cursor = db.rawQuery("select * from kwhdata where month="+month+" and date >="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                list.add(cursor.getFloat(3));

                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }

            //다음달 데이터 가져오기
            int after_month = month+1;
            cursor = db.rawQuery("select * from kwhdata where month="+after_month+" and date <="+resetDate, null); // 이번달 데이터 취합
            while(cursor.moveToNext()) {
                list.add(cursor.getFloat(3));

                str += cursor.getFloat(0)
                        + " : id / "
                        + cursor.getFloat(1)
                        + " : month / "
                        + cursor.getFloat(2)
                        + " : date "
                        + cursor.getFloat(3)
                        + " : kwh "
                        + "\n";
            }



        }

        return list;
    }

    public ArrayList<Float> findData(int month, int date) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        ArrayList<Float> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from kwhdata where month="+month+" and date="+date, null);
        while(cursor.moveToNext()) {
            list.add(cursor.getFloat(3));

            str += cursor.getFloat(0)
                    + " : id / "
                    + cursor.getFloat(1)
                    + " : month / "
                    + cursor.getFloat(2)
                    + " : date "
                    + cursor.getFloat(3)
                    + " : kwh "
                    + "\n";

        }

        db.close();
        return list;
    }

    public void insert (String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        //db.execSQL("insert into mytable values("+month+", "+date+", "+kwh+");");
        db.close();
    }

    public String printData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from kwhdata", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : id / "
                    + cursor.getInt(1)
                    + " : month / "
                    + cursor.getInt(2)
                    + " : date "
                    + cursor.getInt(3)
                    + " : kwh "
                    + "\n";
        }

        db.close();
        return str;
    }
}
