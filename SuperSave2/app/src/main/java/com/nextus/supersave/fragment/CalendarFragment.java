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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nextus.supersave.view.CalendarView;
import com.nextus.supersave.view.CalendarViewAll;
import com.nextus.supersave.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by Iiro Krankka (http://github.com/roughike)
 */
public class CalendarFragment extends Fragment {
    private static final String ARG_TEXT = "ARG_TEXT";
    CircleProgressView circleProgressView;
    CalendarView calendarView;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);

        CalendarFragment sampleFragment = new CalendarFragment();
        sampleFragment.setArguments(args);

        return sampleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.calendar_fragment, container, false);

       // CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);



        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CalendarViewAll cv = ((CalendarViewAll)view.findViewById(R.id.calendar_view_01));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new CalendarViewAll.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
