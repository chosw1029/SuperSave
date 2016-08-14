package com.nextus.supersave.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nextus.supersave.ListStructure;
import com.nextus.supersave.ListViewAdapter;
import com.nextus.supersave.R;
import com.nextus.supersave.db.CustomSQLiteHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by chosw on 2016-05-17.
 */
public class CalendarViewAll extends LinearLayout {

    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    public LinearLayout scrollLayout;


    public ListView listView;
    public ListViewAdapter mAdapter;
    public CustomSQLiteHelper helper;

    public CalendarViewAll calendarViewAll;

    public CalendarViewAll(Context context)
    {
        super(context);
    }

    public CalendarViewAll(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context, attrs);
        calendarViewAll = this;
    }

    public CalendarViewAll(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }
    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.prev_button);
        btnNext = (ImageView)findViewById(R.id.next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
        listView = (ListView) findViewById(R.id.listView);
    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI

        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // long-pressing a day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));

                int month = ((Date) view.getItemAtPosition(position)).getMonth()+1;
                int date = ((Date) view.getItemAtPosition(position)).getDate();

                dialog(view, month, date, position);

               // helper  = new CustomSQLiteHelper( getContext(), "ELECTRO_DATA.db", null, 1);
               // Log.d("FindData", ""+helper.findData(6,27));
                return true;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               showSavedData(parent, position);
            }
        });
    }

    public void showSavedData(AdapterView<?> parent, int position)
    {
        int month = ((Date) parent.getItemAtPosition(position)).getMonth()+1;
        int date = ((Date) parent.getItemAtPosition(position)).getDate();

        helper  = new CustomSQLiteHelper( getContext(), "ELECTRO_DATA.db", null, 1);
        ArrayList<Float> temp_list = helper.findData(month, date);

        mAdapter = new ListViewAdapter(getContext());

        for(int i=0; i<temp_list.size(); i++)
        {
            String temp = ""+month+"월 "+date+"일 "+temp_list.get(i)+"kWh 를 입력하였습니다.";
            mAdapter.addItem(temp);
        }
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("ListViewItem", ""+i);
            }
        });
    }

    public void dialog(final AdapterView<?> view, int m, int d, final int position)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        final View dialog_view = inflater.inflate(R.layout.dialog_input, null);
        final int month = m;
        final int date = d;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("전력량을 입력하세요");
        builder.setView(dialog_view);

        String positiveText = getContext().getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        EditText editText = (EditText) dialog_view.findViewById(R.id.edit_text);
                        float kwh = Float.parseFloat(editText.getText().toString());
                        //int kwh = Integer.parseInt(editText.getText().toString());

                        String insert_query = "insert into kwhdata values(null, "+month+", "+date+", "+kwh+");";

                        helper = new CustomSQLiteHelper( getContext(), "ELECTRO_DATA.db", null, 1);
                        //helper.drop();
                        helper.insert(insert_query);
                        showSavedData(view, position);

                        //Log.d("data_print", ""+helper.printData());

                    }
                });

        String negativeText = getContext().getString(android.R.string.cancel);
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

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events)
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        //calendar.set(Calendar.DAY_OF_MONTH, new Date().getDate());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells  calendar.getActualMaximum(calendar.DAY_OF_MONTH)
        while (cells.size() < 35 )
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);

        txtDate.setText(""+(month+1)+"월");
    }


    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
           // view.setBackgroundResource(0);
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year)
                    {
                        Log.d("eventDays","come");
                       // ImageView imageView = (ImageView) view.findViewById(R.id.calendar_day_icon);
                        //imageView.setImageResource(R.drawable.ic_favorites);
                        // mark this day for event
                       // view.setBackgroundResource(R.background.reminder);
                        break;
                    }
                }
            }

            // clear styling
            ((TextView)view.findViewById(R.id.calendar_day_text)).setTextSize(30.0f);
            ((TextView)view.findViewById(R.id.calendar_day_text)).setTypeface(null, Typeface.NORMAL);
            ((TextView)view.findViewById(R.id.calendar_day_text)).setTextColor(Color.BLACK);
            ((TextView)view.findViewById(R.id.calendar_day_text)).setGravity(Gravity.TOP);

            if (month != today.getMonth() || year != today.getYear())
            {
                // if this day is outside current month, grey it out
                ((TextView)view.findViewById(R.id.calendar_day_text)).setTextColor(getResources().getColor(R.color.greyed_out));
            }
            else if (day == today.getDate())
            {
                // if it is today, set it to blue/bold
                ((TextView)view.findViewById(R.id.calendar_day_text)).setTypeface(null, Typeface.BOLD);
                ((TextView)view.findViewById(R.id.calendar_day_text)).setTextColor(getResources().getColor(R.color.today));
            }
            else if ( position%7 == 0 )
            {
                ((TextView)view.findViewById(R.id.calendar_day_text)).setTextColor(getResources().getColor(R.color.sunday));
            }
            else if ( position%7 == 6 )
            {
                ((TextView)view.findViewById(R.id.calendar_day_text)).setTextColor(getResources().getColor(R.color.saturday));
            }

            helper = new CustomSQLiteHelper( getContext(), "ELECTRO_DATA.db", null, 1);
            ArrayList<ListStructure> list = helper.getData();

            for(int i=0; i<list.size(); i++)
            {
                if( month == list.get(i).getMonth()-1 && day == list.get(i).getDate() )
                {
                    ImageView imageView = (ImageView) view.findViewById(R.id.calendar_day_icon);
                    imageView.setImageResource(R.drawable.ic_favorites);
                }
            }



            // set text
            ((TextView)view.findViewById(R.id.calendar_day_text)).setText(String.valueOf(date.getDate()));



            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onDayLongPress(Date date);
    }

}
