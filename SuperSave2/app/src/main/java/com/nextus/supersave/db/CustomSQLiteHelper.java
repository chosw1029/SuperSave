package com.nextus.supersave.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nextus.supersave.ListStructure;

import java.util.ArrayList;

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

    void delete() {
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
