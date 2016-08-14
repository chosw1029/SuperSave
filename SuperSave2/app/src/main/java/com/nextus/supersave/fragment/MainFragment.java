/*
 * BottomBar library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nextus.supersave.fragment;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nextus.supersave.Calculator;
import com.nextus.supersave.MyApplication;
import com.nextus.supersave.db.CustomSQLiteHelper;
import com.nextus.supersave.view.CalendarView;
import com.nextus.supersave.R;
import com.nextus.supersave.view.DeclareView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

/**
 * Created by Iiro Krankka (http://github.com/roughike)
 */
public class MainFragment extends FragmentView implements View.OnClickListener {
    private static final String ARG_TEXT = "ARG_TEXT";

    @DeclareView ( id = R.id.circleView ) at.grabner.circleprogress.CircleProgressView circleProgressView;

    public CustomSQLiteHelper helper;
    TextView current_money;
    TextView total_kwh;

    public MainFragment(){
    }

    public static MainFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);

        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(args);

        return mainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = ViewMapper.inflateLayout(getContext(), container, R.layout.fragment_main);
        View view = inflateView(R.layout.fragment_main, container);
        //View view = inflater.inflate(R.layout.fragment_main, container, false);

        current_money = (TextView) view.findViewById(R.id.current_money);
        total_kwh = (TextView) view.findViewById(R.id.total_kwh);

        settingCalendar(view);
        settingProgressView(view);

        FloatingActionButton floatingactionButton = (FloatingActionButton) view.findViewById(R.id.floating);
        floatingactionButton.setOnClickListener(this);

        return view;
    }

    public void settingCalendar(View view)
    {
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CalendarView cv = ((CalendarView)view.findViewById(R.id.calendar_view));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void settingProgressView (View view)
    {
        // Progressbar
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.background);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.progress);
        // mProgress.setProgress(25);   // Main Progress
        // mProgress.setSecondaryProgress(50); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 50);
        animation.setDuration(990);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        circleProgressView = (CircleProgressView)view.findViewById(R.id.circleView);

        circleProgressView.setValueAnimated(35.0f);
        circleProgressView.setUnitVisible(false);
        circleProgressView.setTextMode(TextMode.TEXT);
        circleProgressView.setUnitPosition(UnitPosition.BOTTOM);
        circleProgressView.setValue(0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.floating:
                LayoutInflater inflater = getLayoutInflater(null);

                final View dialog_view = inflater.inflate(R.layout.dialog_input, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("전력량을 입력하세요");
                builder.setView(dialog_view);

                String positiveText = getString(android.R.string.ok);

                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // positive button logic
                                EditText editText = (EditText) dialog_view.findViewById(R.id.edit_text);

                                float kwh = Float.parseFloat(editText.getText().toString());

                                // today
                                Date today = new Date();

                                float month = today.getMonth()+1;
                                float date = today.getDate();

                                String insert_query = "insert into kwhdata values(null, "+month+", "+date+", "+kwh+");";
                                helper = new CustomSQLiteHelper( getContext(), "ELECTRO_DATA.db", null, 1);
                                //helper.drop();
                                helper.insert(insert_query);


                                updateData();

                            }
                        });

                String negativeText = getString(android.R.string.cancel);
                builder.setNegativeButton(negativeText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // negative button logic
                                //   mBottomBar.selectTabAtPosition(before_position, true);
                            }
                        });

                // display dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    public void updateData()
    {
        Date today = new Date();
        int month = today.getMonth()+1;

        ArrayList<Float> monthly_sum = MyApplication.mInstance.getHelper().monthly_data(month);
        if(monthly_sum.size() > 0)
        {
            float first_data = monthly_sum.get(0);
            float last_data = monthly_sum.get(monthly_sum.size()-1);
            float subtraction = last_data - first_data;
            MyApplication.mInstance.setTotal_kwh(subtraction);
        }
        else
            MyApplication.mInstance.setTotal_kwh(0);

        Calculator.getInstance().setData(MyApplication.mInstance.getTotal_kwh(), getContext());
        current_money.setText(""+ Calculator.getInstance().getTotal_money()+" 원");

        //String total = String.format("%1.f", MyApplication.mInstance.getTotal_kwh());

        DecimalFormat format = new DecimalFormat(".##");
        String total = format.format(MyApplication.mInstance.getTotal_kwh());

        total_kwh.setText(total+" kWH");

        circleProgressView.setValueAnimated(MyApplication.mInstance.getTotal_kwh()/6);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateData();

    }
}
