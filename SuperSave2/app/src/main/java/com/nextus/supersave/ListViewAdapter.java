package com.nextus.supersave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ReStartAllKill on 2016-06-28.
 */
public class ListViewAdapter extends BaseAdapter {

    private Context mContext = null;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListStructure> mList = new ArrayList<ListStructure>() ;


    // ListViewAdapter의 생성자
    public ListViewAdapter(Context context) {
        super();
        mContext = context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.saved_data_layout, null);

            holder.text = (TextView) convertView.findViewById(R.id.info_text);
            holder.icon = (ImageView) convertView.findViewById(R.id.remove_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(""+mList.get(position).getContent());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return mList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String temp) {
        ListStructure listStructure = null;
        listStructure = new ListStructure() ;
        listStructure.setContent(temp);

        mList.add(listStructure);
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
