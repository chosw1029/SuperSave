package com.nextus.supersave;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by chosw on 2016-05-29.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_list,null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            info = (TextView)itemView.findViewById(R.id.info);

            title.setText("Terry 20' 현대");
            info.setText("Due to a bug, we do not have patches from Preview 1 to Preview 2, so you will need to download a complete install of the IDE. The relevant patching bug is fixed in this build, so we should be able to patch from Preview 2 to Preview 3 next time. ");
        }
    }

}


