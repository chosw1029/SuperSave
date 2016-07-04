package com.nextus.supersave.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nextus.supersave.Calculator;
import com.nextus.supersave.view.DeclareView;
import com.nextus.supersave.R;
import com.nextus.supersave.fragment.MainFragment;
import com.nextus.supersave.lifecycle.CycleControllerActivity;

import java.util.Calendar;

public class MainActivity extends CycleControllerActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    final FragmentTransaction transaction = fragmentManager.beginTransaction();
    final MainFragment fragment = MainFragment.newInstance("mainfragment");

    @DeclareView( id = R.id.calendar_date_display )
    TextView txtDate;

    @DeclareView ( id = R.id.calendar_prev_button )
    ImageView calendar;

    @DeclareView ( id = R.id.adView )
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Super Save");

        setContentView(R.layout.activity_main, true);

        transaction.add(R.id.activity_main, fragment);
        transaction.commit();

        Calendar today = Calendar.getInstance();
        // update title
        txtDate.setText("" + (today.get(Calendar.MONTH) + 1) + "월");
        txtDate.setTextSize(25);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, fragment3).addToBackStack("calendar").commit();
                Intent intent = new Intent(MainActivity.this, CalendarSelectActivity.class);
                startActivity(intent);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("D57D640014965742449F9CCD4947F924").build();
        adView.loadAd(adRequest);

        Calculator.getInstance().setData(379, getApplicationContext());
        Log.e("money", ""+Calculator.getInstance().getTotal_money());

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
      //  mBottomBar.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.actionbar_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }

}
