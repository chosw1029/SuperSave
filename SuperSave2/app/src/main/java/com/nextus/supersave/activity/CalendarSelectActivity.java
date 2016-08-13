package com.nextus.supersave.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.nextus.supersave.MyApplication;
import com.nextus.supersave.db.CustomSQLiteHelper;
import com.nextus.supersave.view.CalendarViewAll;
import com.nextus.supersave.view.DeclareView;
import com.nextus.supersave.R;
import com.nextus.supersave.lifecycle.CycleControllerActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CalendarSelectActivity extends CycleControllerActivity {

    @DeclareView ( id = R.id.calendar_date_display )
    TextView txtDate;


    ImageView calendar;

    @DeclareView ( id = R.id.calendar_view_01 )
    CalendarViewAll cv;


    public int before_position = 0;

    public CustomSQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_fragment, true);


        setTitle("기록");


      //  cv = (CalendarViewAll) findViewById(R.id.calendar_view_01);
        cv.updateCalendar();

        // assign event handler
        cv.setEventHandler(new CalendarViewAll.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {

                Date today = new Date();
                int month = today.getMonth()+1;

                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getApplicationContext(), df.format(date), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
