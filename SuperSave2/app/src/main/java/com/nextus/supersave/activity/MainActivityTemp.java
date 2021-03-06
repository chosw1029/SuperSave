package com.nextus.supersave.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nextus.supersave.Calculator;
import com.nextus.supersave.MyApplication;
import com.nextus.supersave.R;
import com.nextus.supersave.db.CustomSQLiteHelper;
import com.nextus.supersave.fragment.MainFragment;
import com.nextus.supersave.lifecycle.CycleControllerActivity;
import com.nextus.supersave.view.CalendarView;
import com.nextus.supersave.view.DeclareView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

public class MainActivityTemp extends CycleControllerActivity implements View.OnClickListener {

    @DeclareView(id = R.id.current_money)TextView current_money;
    @DeclareView(id = R.id.total_kwh)TextView total_kwh;
    @DeclareView(id = R.id.floating, click = "this")FloatingActionButton floatingactionButton;
    @DeclareView(id = R.id.expect)TextView expect;
    @DeclareView(id = R.id.goal)TextView goal_text;
    //@DeclareView(id = R.id.calendar_prev_button)ImageView calendar;
    @DeclareView(id = R.id.adView)AdView adView;
    @DeclareView(id = R.id.progress)ProgressBar mProgress;
    @DeclareView(id = R.id.circleView)at.grabner.circleprogress.CircleProgressView circleProgressView;

    Calendar today = Calendar.getInstance();

    SharedPreferences sharedPref;

    public CustomSQLiteHelper helper;

    String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp, true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        goal = sharedPref.getString("list_preference", "empty");

        if(goal.contentEquals("empty"))
        {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        setActionBar();
        //setCalendar();
        settingCalendar();
        settingProgressView();

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
        getSupportActionBar().setCustomView(view);

        ((TextView)view.findViewById(R.id.calendar_date_display)).setText("" + (today.get(Calendar.MONTH) + 1) + "월 " + today.get(Calendar.DATE)+"일");

        ImageView calendar = (ImageView)view.findViewById(R.id.calendar_input);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, fragment3).addToBackStack("calendar").commit();
                Intent intent = new Intent(MainActivityTemp.this, CalendarSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    public void settingProgressView() {
        // Progressbar
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.background);
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

    }

    public void settingCalendar() {
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CalendarView cv = ((CalendarView) findViewById(R.id.calendar_view));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getApplicationContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    public void setGoalText(int money) {
        ObjectAnimator animation;
        int temp = money;

        if (goal.contentEquals("empty")) {
            goal_text.setText("목표금액이 설정되있지 않습니다.");
            animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 0);
            animation.setDuration(990);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.start();
        } else {
            int goal_money = Integer.parseInt(goal);
            goal_text.setText("목표금액 : " + goal_money + "원");

            int goal_percent = (temp * 100 / goal_money);
            animation = ObjectAnimator.ofInt(mProgress, "progress", 0, goal_percent);
            animation.setDuration(990);
            animation.setInterpolator(new AccelerateInterpolator());

            animation.start();

            Log.e("Goal_Percent", "총액 : " + money + " // 목표금액 : " + goal_money + " // 퍼센트 : " + goal_percent);
        }


        //mProgress.setProgress(money/goal_money);

    }

    public void updateData() {
        goal = sharedPref.getString("edit_preference", "empty");
        Date today = new Date();
        int month = today.getMonth() + 1;

        //ArrayList<Float> monthly_sum = MyApplication.mInstance.getHelper().monthly_data(month);

        ArrayList<Float> monthly_sum = MyApplication.mInstance.getHelper().detail_data(month, today.getDate());

        // 만약 오늘이 검침일 전이라면 -> 저번달 15 ~ 이번달 15  데이터 가져오고
        // 만약 오늘이 검침일 이후라면 -> 이번달 15 ~ 다음달 15 데이터

        if (monthly_sum.size() > 0) {
            float first_data = monthly_sum.get(0);
            float last_data = monthly_sum.get(monthly_sum.size() - 1);
            float subtraction = last_data - first_data;
            MyApplication.mInstance.setTotal_kwh(subtraction);
        } else
            MyApplication.mInstance.setTotal_kwh(0);

        Calculator.getInstance().setData(MyApplication.mInstance.getTotal_kwh(), getApplicationContext());

        int total_money = Calculator.getInstance().getTotal_money();
        current_money.setText("" + total_money + " 원");

        //String total = String.format("%1.f", MyApplication.mInstance.getTotal_kwh());

        DecimalFormat format = new DecimalFormat(".##");
        String total = format.format(MyApplication.mInstance.getTotal_kwh());

        total_kwh.setText(total + " kWH");

        float total_expect_kwh = 0;
        if(monthly_sum.size() > 1)
        {
            float average = monthly_sum.get(monthly_sum.size()-1) - monthly_sum.get(0);

            average = average / monthly_sum.size();

            int lastDay = 0;                                       //마지막 날짜 변수

            Calendar cal = Calendar.getInstance();

            cal.set(today.getYear(),month-1,1);                        //Calendar에서는 1월이 0이므로 우리가 사용하는 월에서 -1 해줘야 합니다.

            lastDay = cal.getActualMaximum(Calendar.DATE);

            int resetDate = Integer.parseInt(sharedPref.getString("resetDate", "0"));

            // 만약 오늘이 검침일 이전일때와 이후일때
            if( today.getDate() < resetDate ) // 이전
            {
                //int days = resetDate - today.getDate();
                //days += resetDate;
                total_expect_kwh = average * lastDay;
                Log.e("EXPECT_MONEY",""+total_expect_kwh);
                Calculator.getInstance().setExpectData(total_expect_kwh);
            }
            else  // 검침일 이후일때
            {
                //int days = lastDay - resetDate;
                total_expect_kwh = average * lastDay;
                Log.e("EXPECT_MONEY",""+total_expect_kwh);
                Calculator.getInstance().setExpectData(total_expect_kwh);
            }

            expect.setText("이번달 예상 금액 : "+Calculator.getInstance().getExTotal_money()+"원");
        }
        else
            expect.setText("이번달 예상 금액 : 데이터가 부족합니다.");


        circleProgressView.setValueAnimated(MyApplication.mInstance.getTotal_kwh() / 6);
        //circleProgressView.setRimColor(getResources().getColor(R.color.mainColor));
        circleProgressView.setBarColor(Color.rgb(235,251,255), Color.rgb(0,150,250));

        setGoalText(Calculator.getInstance().getExTotal_money());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makeDialog() {
        LayoutInflater inflater = getLayoutInflater();

        final View dialog_view = inflater.inflate(R.layout.dialog_input, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

                        float month = today.getMonth() + 1;
                        float date = today.getDate();

                        String insert_query = "insert into kwhdata values(null, " + month + ", " + date + ", " + kwh + ");";
                        helper = new CustomSQLiteHelper(getApplicationContext(), "ELECTRO_DATA.db", null, 1);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating:
                makeDialog();

                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료하시겠습니까?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dis
                    }
                })
                .create().show();
    }
}
